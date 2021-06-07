package leo.parser

import leo.Atom
import leo.Chain
import leo.EmptyNotation
import leo.LinkNotation
import leo.LiteralAtom
import leo.NameAtom
import leo.Notation
import leo.NotationLine
import leo.NotationLink
import leo.Stack
import leo.array
import leo.atom
import leo.chain
import leo.fieldTo
import leo.fold
import leo.line
import leo.linkTo
import leo.notation
import leo.plus
import leo.reverse

val atomParser: Parser<Atom>
	get() =
		firstCharOneOf(
			literalParser.map { atom(it) },
			nameParser.map { atom(it) })

val chainParser: Parser<Chain>
	get() =
		atomParser.bind { atom ->
			dottedNameStackParser
			unitParser('.')
				.bind { nameParser }
				.stackParser.bind { nameStack ->
					chain(atom).fold(nameStack.reverse) { plus(it) }.parser()
				}
		}

val dottedNameStackParser: Parser<Stack<String>>
	get() =
		unitParser('.')
			.bind { nameParser }
			.stackParser

val notationParser: Parser<Notation>
	get() =
		notationLineParser.stackParser.map { notation(*it.array) }

val notationLinkParser: Parser<NotationLink>
	get() =
		notationLineParser.stackLinkParser.map {
			it.reverse.let {
				notation(*it.tail.array) linkTo it.head
			}
		}

val notationLineParser: Parser<NotationLine>
	get() =
		atomParser.bind { atom ->
			when (atom) {
				is LiteralAtom ->
					dottedNameStackParser.bind { nameStack ->
						unitParser('\n').map {
							line(chain(atom).fold(nameStack.reverse) { plus(it) })
						}
					}
				is NameAtom ->
					firstCharOneOf(
						dottedNameStackParser.bind { nameStack ->
							unitParser('\n').map {
								line(chain(atom).fold(nameStack.reverse) { plus(it) })
							}
						},
						notationRhsParser.map { rhs ->
							when (rhs) {
								is EmptyNotation -> line(chain(atom))
								is LinkNotation -> line(atom.name fieldTo rhs.link)
							}
						})
			}
		}

val notationRhsParser: Parser<Notation>
	get() =
		firstCharOneOf(
			notationIndentedRhsParser,
			notationSpacedRhsParser
		)

val notationIndentedRhsParser: Parser<Notation>
	get() =
		unitParser('\n').bind {
			notationParser.indented
		}

val notationSpacedRhsParser: Parser<Notation>
	get() =
		unitParser(' ').bind {
			notationLineParser.map {
				notation(it)
			}
		}
