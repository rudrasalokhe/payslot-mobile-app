package com.docdirect.app.data.storage

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * AES-256-GCM encryption for medical documents stored on the device. The key lives in the
 * Android Keystore and never leaves it. File layout: [1 byte IV length][IV][ciphertext + GCM tag].
 */
object VaultCipher {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "docdirect_vault_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val TAG_BITS = 128

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    /** Wraps [sink] so everything written to the returned stream is encrypted. Close it to finish. */
    fun openEncryptingStream(sink: OutputStream): OutputStream {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val iv = cipher.iv
        sink.write(iv.size)
        sink.write(iv)
        return CipherOutputStream(sink, cipher)
    }

    /** Wraps [source] so reading from the returned stream yields the decrypted bytes. */
    fun openDecryptingStream(source: InputStream): InputStream {
        val ivLength = source.read()
        if (ivLength !in 12..16) throw IOException("Unrecognised vault file")
        val iv = ByteArray(ivLength)
        var offset = 0
        while (offset < ivLength) {
            val read = source.read(iv, offset, ivLength - offset)
            if (read < 0) throw IOException("Vault file is truncated")
            offset += read
        }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(TAG_BITS, iv))
        return CipherInputStream(source, cipher)
    }
}
