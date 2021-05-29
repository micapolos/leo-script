package leo25.parser

import leo25.base.*
import leo13.*
import leo13.Stack
import leo13.stack
import leo25.*
import leo25.Number

data class Parser<T>(
	val plusCharFn: (Char) -> Parser<T>?,
	val parsedOrNullFn: () -> T?
)

fun <T> Parser<T>.plus(char: Char) = plusCharFn(char)
val <T> Parser<T>.parsedOrNull: T? get() = parsedOrNullFn()
fun <T> parsedParser(parsed: T) = Parser({ null }, { parsed })
fun <T> partialParser(plusCharFn: (Char) -> Parser<T>?) = Parser(plusCharFn) { null }
fun <T> T.parser(plusCharFn: (Char) -> Parser<T>?) = Parser(plusCharFn) { this }
fun <T> T.parser(): Parser<T> = parser { null }
fun <T> nullParser(): Parser<T> = Parser({ null }, { null })

fun parser(string: String, startIndex: Int): Parser<String> =
	if (startIndex == string.length) parsedParser(string)
	else partialParser { char ->
		notNullIf(char == string[startIndex]) {
			parser(string, startIndex.inc())
		}
	}

fun parser(string: String): Parser<String> = parser(string, 0)

fun unitParser(char: Char): Parser<Unit> = parser(char).map { }
fun unitParser(string: String): Parser<Unit> = parser(string).map { }

fun charParser(fn: (Char) -> Boolean): Parser<Char> =
	partialParser { char ->
		notNullIf(fn(char)) {
			parsedParser(char)
		}
	}

val charParser: Parser<Char> get() = charParser { true }
fun oneOfCharParser(chars: String) = charParser { chars.contains(it) }
fun noneOfCharParser(chars: String) = charParser { !chars.contains(it) }

fun parser(char: Char): Parser<Char> = charParser { it == char }
val letterCharParser: Parser<Char>
	get() = charParser {
		it.isLetter() && it.isLowerCase()
	}
val digitCharParser: Parser<Char> get() = charParser { it.isDigit() }

fun <T> Parser<T>.enclosedWith(left: Parser<Unit>, right: Parser<Unit> = left): Parser<T> =
	left.bind {
		bind { string ->
			right.bind {
				parsedParser(string)
			}
		}
	}

fun <T> Parser<T>.enclosedWith(leftChar: Char, rightChar: Char): Parser<T> =
	enclosedWith(unitParser(leftChar), unitParser(rightChar))

val <T> Parser<T>.parenthesised: Parser<T> get() = enclosedWith('(', ')')

fun <T> parser(stack: Stack<T>, parser: Parser<T>): Parser<Stack<T>> =
	Parser(
		{ char -> parser.plus(char)?.let { parser(stack, parser, it) } },
		{ stack }
	)

fun <T> Parser<Unit>.unitThen(parser: Parser<T>): Parser<T> =
	bind { parser }

fun <T> Parser<T>.thenUnit(parser: Parser<Unit>): Parser<T> =
	bind { item -> parser.map { item } }

fun <T> parser(stack: Stack<T>, parser: Parser<T>, partialParser: Parser<T>): Parser<Stack<T>> =
	Parser(
		{ char ->
			partialParser.plus(char).let { newPartialParserOrNull ->
				if (newPartialParserOrNull != null) parser(stack, parser, newPartialParserOrNull)
				else partialParser.parsedOrNull?.let { parser(stack.push(it), parser).plus(char) }
			}
		},
		{ partialParser.parsedOrNull?.let { stack.push(it) } }
	)

val Stack<Char>.charPushParser: Parser<Stack<Char>>
	get() =
		parser { push(it).charPushParser }

val charStackParser: Parser<Stack<Char>>
	get() =
		stack<Char>().charPushParser

fun <T> stackParser(parser: Parser<T>): Parser<Stack<T>> =
	parser(stack(), parser)

fun <T> Stack<T>.pushParser(parser: Parser<T>): Parser<Stack<T>> =
	parser(this, parser)

val <T> Parser<T>.stackParser: Parser<Stack<T>>
	get() =
		stack<T>().pushParser(this)

val <T> Parser<T>.stackLinkParser: Parser<StackLink<T>>
	get() =
		stackParser.map { it.linkOrNull }

fun <T, O> Parser<T>.map(fn: (T) -> O?): Parser<O> =
	Parser(
		{ char -> plus(char)?.map(fn) },
		{ parsedOrNull?.let { fn(it) } }
	)

fun <T> Parser<T>.doing(plusCharFn: (Char) -> Parser<T>?): Parser<T> =
	Parser(plusCharFn, parsedOrNullFn)

fun <O> Parser<*>.withParsed(fn: () -> O?): Parser<O> =
	Parser<O>({ char -> plus(char)?.withParsed { null } }, { fn() })

fun <T, O> Parser<T>.bind(fn: (T) -> Parser<O>): Parser<O> =
	Parser(
		{ char ->
			plus(char).let { newParserOrNull ->
				if (newParserOrNull != null) newParserOrNull.bind(fn)
				else parsedOrNull?.let { fn(it).plus(char) }
			}
		},
		{ parsedOrNull?.let { fn(it).parsedOrNull } })

fun <T> Parser<T>.firstCharOr(parser: Parser<T>): Parser<T> =
	Parser(
		{ char ->
			plus(char).let { newParserOrNull ->
				if (newParserOrNull != null) newParserOrNull
				else parser.plus(char)
			}
		},
		{ parsedOrNull ?: parser.parsedOrNull }
	)

fun <T> firstCharOneOf(vararg parsers: Parser<T>): Parser<T> =
	nullParser<T>().fold(parsers) { firstCharOr(it) }

fun <T> oneOf(vararg parsers: Parser<T>): Parser<T> =
	nullParser<T>().fold(parsers) { or(it) }

fun <T> Parser<T>.or(parser: Parser<T>, charStack: Stack<Char>): Parser<T> =
	Parser(
		{ char ->
			plus(char).let { newParserOrNull ->
				if (newParserOrNull != null) newParserOrNull.or(parser, charStack.push(char))
				else parser.orNullFold(charStack.seq.reverse) { plus(it) }?.plus(char)
			}
		},
		{ parsedOrNull ?: parser.parsedOrNull }
	)

fun <T> Parser<T>.or(parser: Parser<T>): Parser<T> =
	or(parser, stack())

fun <T> Parser<T>.plus(string: String): Parser<T>? =
	orNullFold(string.charSeq) { plus(it) }

fun <T> Parser<T>.parsed(string: String) =
	plus(string)?.parsedOrNull

val nameParser: Parser<String>
	get() =
		letterCharParser.stackLinkParser.map {
			it.asStack.charString
		}

val positiveNumberParser: Parser<Number>
	get() =
		digitCharParser.stackLinkParser.map {
			it.asStack.charString.numberOrNull
		}

val <T> Parser<T>.isPresentParser: Parser<Boolean>
	get() =
		map { true }.firstCharOr(parsedParser(false))

val numberParser: Parser<Number>
	get() =
		unitParser("-").isPresentParser.bind { negated ->
			positiveNumberParser.map { number ->
				number.runIf(negated) { unaryMinus() }
			}
		}

val literalParser: Parser<Literal>
	get() =
		textParser.map { literal(it) }
			.firstCharOr(numberParser.map { literal(it) })

val escapeCharParser: Parser<Char>
	get() =
		partialParser { char ->
			when (char) {
				'\\' -> parsedParser('\\')
				'n' -> parsedParser('\n')
				't' -> parsedParser('\t')
				'"' -> parsedParser('"')
				else -> null
			}
		}

val escapeSequenceCharParser: Parser<Char>
	get() =
		unitParser('\\').bind { escapeCharParser }

val textCharParser = noneOfCharParser("\"")

val textBodyParser: Parser<String>
	get() =
		escapeSequenceCharParser
			.firstCharOr(textCharParser)
			.stackParser
			.map { it.charString }

val textParser: Parser<String>
	get() = textBodyParser.enclosedWith(unitParser('"'))

object Tab

val tabUnitParser: Parser<Unit> get() = unitParser("  ")

val tab get() = Tab
val tabParser: Parser<Tab> get() = tabUnitParser.map { Tab }

val Int.maxIndentUnitParser: Parser<Unit>
	get() =
		tabParser.stackParser.map { Unit.orNullIf(it.size > this) }

fun <T> Parser<T>.stackLinkSeparatedBy(parser: Parser<Unit>): Parser<StackLink<T>> =
	bind { first ->
		stack(first).pushParser(parser.unitThen(this)).map { it.linkOrNull }
	}

val Parser<*>.countParser: Parser<Int>
	get() =
		stackParser.map { it.size }

val stringParser: Parser<String>
	get() =
		charStackParser.map { it.charString }

fun Parser<Unit>.repeat(times: Int): Parser<Unit> =
	if (times <= 0) Unit.parser()
	else bind { repeat(times.dec()) }

val <T> Parser<T>.withoutEmptyLines get() = withoutEmptyLines(true)

fun <T> Parser<T>.withoutEmptyLines(isLineStart: Boolean): Parser<T> =
	Parser(
		{ char ->
			if (char == '\n')
				if (isLineStart) withoutEmptyLines(true)
				else plus(char)?.withoutEmptyLines(true)
			else plus(char)?.withoutEmptyLines(false)
		}, {
			parsedOrNull
		})

val <T> Parser<T>.withoutTrailingSpaces get() = withoutTrailingSpaces(0)

fun <T> Parser<T>.withoutTrailingSpaces(trailingSpaceCount: Int): Parser<T> =
	Parser(
		{ char ->
			when (char) {
				'\n' -> plus(char)?.withoutTrailingSpaces(0)
				' ' -> withoutTrailingSpaces(trailingSpaceCount.inc())
				else -> orNull.iterate(trailingSpaceCount) { this?.plus(' ') }?.plus(char)?.withoutTrailingSpaces(0)
			}
		}, {
			parsedOrNull
		})

val <T> Parser<T>.addingMissingNewline get() = addingMissingNewline(needsNewline = false)

fun <T> Parser<T>.addingMissingNewline(needsNewline: Boolean): Parser<T> =
	Parser(
		{ char ->
			plus(char)?.addingMissingNewline(needsNewline = char != '\n')
		}, {
			orNull.runIf(needsNewline) { this?.plus('\n') }?.parsedOrNull
		})
