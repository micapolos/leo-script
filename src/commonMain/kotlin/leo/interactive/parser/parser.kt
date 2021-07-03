@file:Suppress("unused", "UNUSED_PARAMETER")

package leo.interactive.parser

import leo.Stack
import leo.StackLink
import leo.interactive.End
import leo.interactive.Token

data class Letter(val char: Char)
data class Digit(val int: Int)

data class Word(val letterStackLink: StackLink<Letter>)
data class Number(val digitStackLink: StackLink<Digit>)

data class NumberNegative(val numberOrNull: Number?)

sealed class NumberPrefix
data class NegativeNumberPrefix(val negative: NumberNegative): NumberPrefix()
data class NumberNumberPrefix(val number: Number): NumberPrefix()

object Escape
data class TextItemPrefix(val escape: Escape)

data class CharEscaped(val char: Char)

sealed class TextItem
data class CharTextItem(val char: Char): TextItem()
data class EscapedTextItem(val escaped: CharEscaped): TextItem()

data class TextOpening(val itemStack: Stack<TextItem>, val itemPrefixOrNull: TextItemPrefix?)

sealed class TextPrefix
data class OpeningTextPrefix(val opening: TextOpening): TextPrefix()
data class StringTextPrefix(val string: String): TextPrefix()

sealed class LiteralPrefix
data class TextLiteralPrefix(val textPrefix: TextPrefix): LiteralPrefix()
data class NumberLiteralPrefix(val numberPrefix: NumberPrefix): LiteralPrefix()

sealed class AtomPrefix
data class WordAtomPrefix(val word: Word): AtomPrefix()
data class LiteralAtomPrefix(val literalPrefix: LiteralPrefix): AtomPrefix()

data class Tab(val endStackLink: StackLink<End>)
data class TabPrefix(val tab: Tab)

data class Indent(val tabStack: Stack<Tab>)
data class IndentPrefix(val indent: Indent, val tabPrefixOrNull: TabPrefix?)
data class IndentSuffix(val indent: Indent)
data class Header(val prefix: IndentPrefix, val suffix: IndentSuffix)

sealed class Spaceable
data class AtomPrefixSpaceable(val atomPrefixOrNull: AtomPrefix?): Spaceable()
data class SpacedSpaceable(val spaced: Spaced): Spaceable()
data class Spaced(val spaceable: Spaceable)

object Comma

sealed class Commable
data class SpaceableCommable(val spaceable: Spaceable): Commable()
data class CommaCommable(val comma: Comma): Commable()

data class Body(val indent: Indent, val commable: Commable)

sealed class Line
data class HeaderLine(val header: Header): Line()
data class BodyLine(val body: Body): Line()

data class Tokens(val tokenStack: Stack<Token>) { override fun toString() = scriptLine.toString() }
data class TokensPrefix(val tokens: Tokens, val line: Line) { override fun toString() = scriptLine.toString() }

data class Tokenizer<out V>(val tokens: Tokens, val v: V)