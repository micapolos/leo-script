package leo.parser

import leo.base.charSeq
import leo.base.fold
import leo.throwError
import leo.value

data class LocatingParser<T>(
	val location: Location,
	val parser: Parser<T>
)

fun <T> LocatingParser<T>.plus(char: Char): LocatingParser<T> =
	LocatingParser(
		if (char == '\n') location.newLine else location.nextColumn,
		parser.plus(char) ?: throwParserError()
	)

fun <T> LocatingParser<T>.plus(string: String): LocatingParser<T> =
	fold(string.charSeq) { plus(it) }

fun <T, O> LocatingParser<T>.throwParserError(): O =
	location.script.value.throwError()

fun <T> Parser<T>.parseOrThrow(string: String): T =
	LocatingParser(startLocation, this)
		.plus(string)
		.run { parser.parsedOrNull ?: throwParserError() }

