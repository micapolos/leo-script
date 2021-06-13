package leo

import leo.named.evaluator.evaluate as typedEvaluate
import leo.base.print
import leo.java.io.inString
import leo.natives.fileText
import leo.parser.scriptOrThrow
import java.io.File

val typed = false

fun main(args: Array<String>) {
	try {
		readText(args).scriptOrThrow.run {
			if (typed) typedEvaluate else evaluate
		}
	} catch (e: ValueError) {
		value("parser" fieldTo e.value).errorValue.string
	}.print
}

fun readText(args: Array<String>) =
	if (args.isEmpty()) inString
	else try {
		File(args[0]).readText()
	} catch (e: Exception) {
		use(args[0], *args.copyOfRange(1, args.size)).fileNameString.fileText
	}
