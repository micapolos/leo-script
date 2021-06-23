package leo

import leo.base.print
import leo.java.io.inString
import leo.named.compiler.CompileError
import leo.named.evaluator.preludeEvaluate
import leo.natives.fileText
import leo.parser.scriptOrThrow
import java.io.File

val typed = false

fun main(args: Array<String>) {
	try {
		readText(args).scriptOrThrow.run {
			if (typed) preludeEvaluate else evaluate
		}
	} catch (e: ValueError) {
		value("parser" fieldTo e.value).errorValue.string
	} catch (e: CompileError) {
		e.toString()
	}.print
}

fun readText(args: Array<String>) =
	if (args.isEmpty()) inString
	else try {
		File(args[0]).readText()
	} catch (e: Exception) {
		use(args[0], *args.copyOfRange(1, args.size)).fileNameString.fileText
	}
