package com.docdirect.app.data.storage

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
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

    private const val TAG = "FileStorageHelper"
    private const val VAULT_DIR = "vault"
    private const val ENCRYPTED_SUFFIX = ".enc"
    private const val VIEW_CACHE_DIR = "vault_view"
    private const val VIEW_CACHE_MAX_AGE_MS = 10 * 60 * 1000L
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

    /**
     * Copies the picked document into the app's private vault folder, encrypted with
     * AES-256-GCM (see [VaultCipher]), and returns the encrypted copy.
     */
    suspend fun importToVault(context: Context, file: PickedFile): File = withContext(Dispatchers.IO) {
        val dir = vaultDir(context).apply { mkdirs() }
        val target = File(dir, "${UUID.randomUUID()}.${extensionFor(file)}$ENCRYPTED_SUFFIX")
        try {
            val input = context.contentResolver.openInputStream(file.uri)
                ?: throw IOException("Unable to open the selected file")
            input.use { source ->
                VaultCipher.openEncryptingStream(target.outputStream()).use { sink ->
                    copyLimited(source, sink)
                }
            }
        } catch (e: Exception) {
            target.delete()
            throw e
        }
        target
    }

    /** True when [path] is a vault file this helper can decrypt and show. */
    fun isStoredOnDevice(context: Context, path: String): Boolean {
        return resolveVaultFile(context, path)?.exists() == true
    }

    /**
     * Opens a stored document in the device's native reader (Google Drive PDF viewer, Adobe
     * Reader, Photos...) through an ACTION_VIEW intent. Encrypted files are decrypted into a
     * short-lived cache file that is exposed through FileProvider and purged later.
     * Returns null on success, otherwise a user-facing error message.
     */
    suspend fun openStoredDocument(context: Context, path: String, title: String): String? =
        launchDocumentIntent(context, path, title, share = false)

    /** Shares the real stored document (decrypted copy) with another app. */
    suspend fun shareStoredDocument(context: Context, path: String, title: String): String? =
        launchDocumentIntent(context, path, title, share = true)

    /** Deletes decrypted view copies older than [maxAgeMs]; pass 0 to remove all of them. */
    fun purgeViewCache(context: Context, maxAgeMs: Long = VIEW_CACHE_MAX_AGE_MS) {
        try {
            val root = File(context.cacheDir, VIEW_CACHE_DIR)
            val cutoff = System.currentTimeMillis() - maxAgeMs
            root.listFiles()?.forEach { entry ->
                if (maxAgeMs <= 0L || entry.lastModified() < cutoff) entry.deleteRecursively()
            }
        } catch (_: Exception) {
        }
    }

    private suspend fun launchDocumentIntent(
        context: Context,
        path: String,
        title: String,
        share: Boolean
    ): String? {
        val prepared = try {
            withContext(Dispatchers.IO) { prepareViewableCopy(context, path, title) }
        } catch (e: Exception) {
            Log.e(TAG, "Unable to prepare document", e)
            return "This document could not be decrypted on this device."
        } ?: return "This document is no longer stored on this device."

        val (file, mime) = prepared
        return try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = if (share) {
                Intent(Intent.ACTION_SEND).apply {
                    type = mime
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, title)
                }
            } else {
                Intent(Intent.ACTION_VIEW).apply { setDataAndType(uri, mime) }
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.clipData = ClipData.newRawUri(title, uri)

            val chooser = Intent.createChooser(intent, if (share) "Share document" else "Open with").apply {
                if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            null
        } catch (e: ActivityNotFoundException) {
            "No app on this device can open ${if (mime == MIME_PDF) "PDF" else "image"} files."
        } catch (e: Exception) {
            Log.e(TAG, "Unable to open document", e)
            "Could not open the document."
        }
    }

    /** Returns a readable file (decrypted if needed) and its MIME type, or null if the file is gone. */
    private fun prepareViewableCopy(context: Context, path: String, title: String): Pair<File, String>? {
        val stored = resolveVaultFile(context, path)?.takeIf { it.exists() } ?: return null
        val mime = mimeForStoredPath(stored.name)
        if (!stored.name.endsWith(ENCRYPTED_SUFFIX)) return stored to mime

        val ext = stored.name.removeSuffix(ENCRYPTED_SUFFIX).substringAfterLast('.')
        val safeName = title.replace(Regex("[^A-Za-z0-9._ -]"), "_").trim().ifBlank { "document" }
            .let { if (it.endsWith(".$ext", ignoreCase = true)) it else "$it.$ext" }
        val dir = File(context.cacheDir, "$VIEW_CACHE_DIR/${UUID.randomUUID()}").apply { mkdirs() }
        val out = File(dir, safeName)
        try {
            stored.inputStream().use { raw ->
                VaultCipher.openDecryptingStream(raw).use { plain ->
                    out.outputStream().use { sink -> plain.copyTo(sink) }
                }
            }
        } catch (e: Exception) {
            dir.deleteRecursively()
            throw e
        }
        return out to mime
    }

    /** Only paths inside the private vault folder are ever opened. */
    private fun resolveVaultFile(context: Context, path: String): File? {
        if (path.isBlank()) return null
        return try {
            val file = File(path)
            if (file.canonicalPath.startsWith(vaultDir(context).canonicalPath + File.separator)) file else null
        } catch (_: Exception) {
            null
        }
    }

    private fun mimeForStoredPath(fileName: String): String {
        return when (fileName.removeSuffix(ENCRYPTED_SUFFIX).substringAfterLast('.').lowercase(Locale.ROOT)) {
            "pdf" -> MIME_PDF
            "png" -> MIME_PNG
            else -> MIME_JPEG
        }
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
