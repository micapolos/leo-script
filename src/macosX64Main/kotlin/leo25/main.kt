package leo25

import leo.base.print
import leo14.literal
import leo25.natives.fileText
import leo25.parser.scriptOrThrow

fun main(args: Array<String>) {
	try {
		environment().evaluate(readText(args).scriptOrThrow)
	} catch (e: ValueError) {
		value("parser" fieldTo e.value).errorValue.string
	} catch (e: Exception) {
		value(field(literal(e.toString()))).errorValue.string
	}.print
}

fun readText(args: Array<String>) =
	use(args[0], *args.copyOfRange(1, args.size)).fileString.fileText
