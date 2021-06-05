package scheme

import leo.java.lang.exec
import leo.push
import leo.stack
import leo.toList
import java.io.File

val Scheme.eval: Scheme
	get() {
		val file = File.createTempFile("script", ".ss")
		file.deleteOnExit()
		file.writeText("(display $string)")
		return file.run.scheme
	}

val File.run: String
	get() =
		stack<String>()
			.push("chez")
			.push("--optimize-level")
			.push("3")
			.push("--script")
			.push(absolutePath)
			.toList()
			.toTypedArray()
			.let { exec(*it) }
