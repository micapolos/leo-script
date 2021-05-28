package leo25.natives

import java.io.File

actual val String.fileText: String
	get() = File(this).readText()