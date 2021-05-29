package leo25.java.io

import java.io.File
import java.nio.file.Path

val Path.file
	get() =
		toFile()

fun <R> String.inTempFile(extension: String, fn: (File) -> R): R {
	val tempFile = File.createTempFile("tmp", ".$extension")
	try {
		tempFile.writeText(this)
		return fn(tempFile)
	} finally {
		tempFile.delete()
	}
}
