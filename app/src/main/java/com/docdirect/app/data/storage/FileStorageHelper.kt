package com.docdirect.app.data.storage

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import java.util.UUID

/**
 * Helper for documents picked through the Storage Access Framework
 * (ActivityResultContracts.GetContent). It reads the real name / type / size of the picked
 * file, validates it and copies it into app-private storage, because the content Uri returned by
 * the picker is only readable for a short time and cannot be reopened later.
 */
object FileStorageHelper {

    const val MIME_PDF = "application/pdf"
    const val MIME_JPEG = "image/jpeg"
    const val MIME_PNG = "image/png"

    /** Maximum accepted document size. */
    const val MAX_FILE_BYTES = 20L * 1024 * 1024

    private const val VAULT_DIR = "vault"
    private val ALLOWED_MIME_TYPES = setOf(MIME_PDF, MIME_JPEG, MIME_PNG)

    data class PickedFile(
        val uri: Uri,
        val displayName: String,
        val mimeType: String,
        val sizeBytes: Long
    ) {
        val sizeLabel: String get() = formatSize(sizeBytes)
        val isPdf: Boolean get() = mimeType == MIME_PDF
        val typeLabel: String get() = if (isPdf) "PDF" else if (mimeType == MIME_PNG) "PNG image" else "JPG image"
    }

    /** Reads display name, MIME type and size of a picked document. */
    fun describe(context: Context, uri: Uri): PickedFile {
        val resolver = context.contentResolver
        var name = ""
        var size = -1L

        try {
            resolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIndex >= 0 && !cursor.isNull(nameIndex)) name = cursor.getString(nameIndex)
                    if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) size = cursor.getLong(sizeIndex)
                }
            }
        } catch (_: Exception) {
            // Fall through to the fallbacks below
        }

        if (name.isBlank()) {
            name = uri.lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':')
                ?.ifBlank { null } ?: "Document"
        }
        if (size < 0) {
            size = try {
                resolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: -1L
            } catch (_: Exception) {
                -1L
            }
        }

        return PickedFile(uri, name, normalizeMime(resolver.getType(uri), name), size)
    }

    /** Returns a user-facing error message, or null when the file can be stored. */
    fun validate(file: PickedFile): String? {
        return when {
            file.mimeType !in ALLOWED_MIME_TYPES -> "Only PDF, JPG or PNG files are supported."
            file.sizeBytes == 0L -> "That file is empty."
            file.sizeBytes > MAX_FILE_BYTES -> "File is larger than ${MAX_FILE_BYTES / (1024 * 1024)} MB."
            else -> null
        }
    }

    /** Copies the picked document into the app's private vault folder and returns the copy. */
    suspend fun importToVault(context: Context, file: PickedFile): File = withContext(Dispatchers.IO) {
        val dir = vaultDir(context).apply { mkdirs() }
        val target = File(dir, "${UUID.randomUUID()}.${extensionFor(file)}")
        try {
            val input = context.contentResolver.openInputStream(file.uri)
                ?: throw IOException("Unable to open the selected file")
            input.use { source ->
                target.outputStream().use { sink -> copyLimited(source, sink) }
            }
        } catch (e: Exception) {
            target.delete()
            throw e
        }
        target
    }

    /** Deletes a file previously created by [importToVault]; paths outside the vault are ignored. */
    fun deleteStoredFile(context: Context, path: String) {
        if (path.isBlank()) return
        try {
            val file = File(path)
            if (file.canonicalPath.startsWith(vaultDir(context).canonicalPath + File.separator)) {
                file.delete()
            }
        } catch (_: Exception) {
        }
    }

    fun formatSize(bytes: Long): String = when {
        bytes < 0 -> "Unknown size"
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
    }

    /** Adds the real file extension to a user typed title when it has none. */
    fun titleWithExtension(title: String, file: PickedFile): String {
        val trimmed = title.trim()
        val ext = extensionFor(file)
        return if (trimmed.endsWith(".$ext", ignoreCase = true) ||
            (ext == "jpg" && trimmed.endsWith(".jpeg", ignoreCase = true))
        ) trimmed else "$trimmed.$ext"
    }

    /** File name without its extension, handy as a default title. */
    fun baseName(displayName: String): String = displayName.substringBeforeLast('.', displayName)

    fun extensionFor(file: PickedFile): String = when (file.mimeType) {
        MIME_PDF -> "pdf"
        MIME_PNG -> "png"
        else -> "jpg"
    }

    private fun vaultDir(context: Context): File = File(context.filesDir, VAULT_DIR)

    private fun normalizeMime(reported: String?, name: String): String {
        val type = reported?.lowercase(Locale.ROOT)
        val resolved = if (type.isNullOrBlank() || type == "application/octet-stream") {
            val ext = name.substringAfterLast('.', "").lowercase(Locale.ROOT)
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: type.orEmpty()
        } else {
            type
        }
        return if (resolved == "image/jpg") MIME_JPEG else resolved
    }

    private fun copyLimited(source: InputStream, sink: OutputStream) {
        val buffer = ByteArray(16 * 1024)
        var total = 0L
        while (true) {
            val read = source.read(buffer)
            if (read < 0) break
            total += read
            if (total > MAX_FILE_BYTES) throw IOException("File is larger than ${MAX_FILE_BYTES / (1024 * 1024)} MB.")
            sink.write(buffer, 0, read)
        }
    }
}
