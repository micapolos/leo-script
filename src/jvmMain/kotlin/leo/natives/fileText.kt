package leo.natives

import java.io.File

actual val String.fileText: String
  get() = File(this).readText()