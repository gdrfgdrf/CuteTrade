package io.github.gdrfgdrf.cutetrade.utils

import com.google.protobuf.Message
import org.xerial.snappy.Snappy
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Protobuf<T : Message> {
    var message: T? = null
    var storeFile: File? = null
    var modCount: Int = 0

    fun rebuild(
        function: (T?) -> T
    ) {
        message = function(message)
    }

    fun mod() {
        save()
    }

    fun save() {
        if (message == null ||
            storeFile == null ||
            !storeFile!!.exists() ||
            !storeFile!!.isFile) {
            return
        }

        val fileOutputStream = FileOutputStream(storeFile!!)
        val buffOutputStream = BufferedOutputStream(fileOutputStream)
        val bytes = Snappy.compress(message!!.toByteArray())

        buffOutputStream.write(bytes)
        buffOutputStream.close()
        fileOutputStream.close()
    }

    companion object {
        fun <T : Message> get(
            storeFile: File,
            parse: (ByteArray) -> T,
        ): Protobuf<T>? {
            if (!storeFile.exists() || !storeFile.isFile) {
                return null
            }

            val fileInputStream = FileInputStream(storeFile)
            val bufferedInputStream = BufferedInputStream(fileInputStream)
            var bytes = bufferedInputStream.readAllBytes()

            fileInputStream.close()
            bufferedInputStream.close()

            bytes = Snappy.uncompress(bytes)
            val t = parse(bytes)

            val protobuf = Protobuf<T>()
            protobuf.message = t
            protobuf.storeFile = storeFile

            return protobuf
        }
    }
}