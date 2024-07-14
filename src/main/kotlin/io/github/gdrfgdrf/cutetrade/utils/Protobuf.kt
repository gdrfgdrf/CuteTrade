/*
 * Copyright 2024 CuteTrade's contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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