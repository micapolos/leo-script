package leo25.parser

import leo25.fold
import leo25.reverse
import leo25.*
import leo25.LiteralAtom
import leo25.NameAtom

val preprocessingScriptParser: Parser<Script> =
	scriptParser.preprocessing

val <T> Parser<T>.preprocessing: Parser<T>
	get() =
		withoutTrailingSpaces.withoutEmptyLines.addingMissingNewline

val scriptParser: Parser<Script>
	get() =
		scriptBlockParser.stackParser.map {
			script().fold(it.reverse) { plus(it) }
		}

val scriptFieldParser: Parser<ScriptField>
	get() =
		nameParser.bind { name ->
			scriptRhsParser.map { rhs ->
				name fieldTo rhs
			}
		}

val scriptRhsParser: Parser<Script>
	get() =
		firstCharOneOf(
			scriptIndentedRhsParser,
			scriptSpacedRhsParser
		)

val scriptIndentedRhsParser: Parser<Script>
	get() =
		unitParser('\n').bind {
			scriptParser.indented
		}

val scriptSpacedRhsParser: Parser<Script>
	get() =
		unitParser(' ').bind {
			scriptBlockParser
		}

val scriptBlockParser: Parser<Script>
	get() =
		atomParser.bind { atom ->
			when (atom) {
				is LiteralAtom ->
					dottedNameStackParser.bind { nameStack ->
						unitParser('\n').map {
							script(line(atom.literal)).fold(nameStack.reverse) { plus(it lineTo script()) }
						}
					}
				is NameAtom ->
					firstCharOneOf(
						scriptRhsParser.map {
							script(atom.name lineTo it)
						},
						dottedNameStackParser.bind { nameStack ->
							unitParser('\n').map {
								script(line(atom.name)).fold(nameStack.reverse) { plus(it lineTo script()) }
							}
						})
			}
		}
