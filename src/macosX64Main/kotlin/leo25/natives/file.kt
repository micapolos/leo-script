package leo25.natives

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

actual val String.fileText: String
  get() = readAllText(this)

fun readAllText(filePath: String): String {
  val returnBuffer = StringBuilder()
  val file = fopen(filePath, "r") ?: throw IllegalArgumentException("Cannot open input file $filePath")

  try {
    memScoped {
      val readBufferLength = 64 * 1024
      val buffer = allocArray<ByteVar>(readBufferLength)
      var line = fgets(buffer, readBufferLength, file)?.toKString()
      while (line != null) {
        returnBuffer.append(line)
        line = fgets(buffer, readBufferLength, file)?.toKString()
      }
    }
  } finally {
    fclose(file)
  }

  return returnBuffer.toString()
}