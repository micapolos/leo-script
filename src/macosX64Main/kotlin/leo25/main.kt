package leo25

import leo.base.print
import leo25.natives.fileText
import leo25.parser.scriptOrThrow

fun main(args: Array<String>) {
	try {
		environment().interpret(readText(args).scriptOrThrow)
	} catch (e: ValueError) {
		value("parser" fieldTo  e.value).errorValue.string
	}.print
}

fun readText(args: Array<String>) =
	use(args[0], *args.copyOfRange(1, args.size)).fileString.fileText
