package leo.interactive.parser

import leo.base.charSeq
import leo.base.fold
import leo.base.notNullOrError
import leo.base.orNull
import leo.interactive.End
import leo.interactive.Token
import leo.isEmpty
import leo.push
import leo.pushAll
import leo.reverse
import leo.stack
import leo.stackLink

fun letter(char: Char): Letter =
	char.letterOrNull.notNullOrError("letter($char)")

fun digit(int: Int): Digit =
	int.digitOrNull.notNullOrError("digit($int)")

fun word(letter: Letter, vararg letters: Letter): Word =
	Word(stackLink(letter, *letters))

fun number(digit: Digit, vararg digits: Digit): Number =
	Number(stackLink(digit, *digits))

val numberNegative get() = NumberNegative(null)
fun negative(number: Number): NumberNegative = NumberNegative(number)

fun numberPrefix(negative: NumberNegative): NumberPrefix = NegativeNumberPrefix(negative)
fun numberPrefix(number: Number): NumberPrefix = NumberNumberPrefix(number)

val escape get() = Escape
fun escaped(char: Char) = CharEscaped(char)

fun textItem(char: Char): TextItem = CharTextItem(char)
fun textItem(escaped: CharEscaped): TextItem = EscapedTextItem(escaped)

fun textItemPrefix(escape: Escape) = TextItemPrefix(escape)

fun textOpening(vararg items: TextItem) = TextOpening(stack(*items), null)
fun TextOpening.with(prefix: TextItemPrefix) = copy(itemPrefixOrNull = prefix)

fun textPrefix(string: String): TextPrefix = StringTextPrefix(string)
fun textPrefix(opening: TextOpening): TextPrefix = OpeningTextPrefix(opening)

fun literalPrefix(textPrefix: TextPrefix): LiteralPrefix = TextLiteralPrefix(textPrefix)
fun literalPrefix(numberPrefix: NumberPrefix): LiteralPrefix = NumberLiteralPrefix(numberPrefix)

fun atomPrefix(word: Word): AtomPrefix = WordAtomPrefix(word)
fun atomPrefix(prefix: LiteralPrefix): AtomPrefix = LiteralAtomPrefix(prefix)

fun tab(end: End, vararg ends: End) = Tab(stackLink(end, *ends))
fun prefix(tab: Tab) = TabPrefix(tab)

fun indent(vararg tabs: Tab) = Indent(stack(*tabs))
fun suffix(indent: Indent) = IndentSuffix(indent.tabStack.reverse.let(::Indent))
fun prefix(indent: Indent, tabPrefixOrNull: TabPrefix?) = IndentPrefix(indent, tabPrefixOrNull)

fun header(prefix: IndentPrefix, suffix: IndentSuffix) = Header(prefix, suffix)
fun header() = header(prefix(indent(), null), suffix(indent()))

fun spaceable(spaced: Spaced): Spaceable = SpacedSpaceable(spaced)
fun spaceable(atomPrefixOrNull: AtomPrefix? = null): Spaceable = AtomPrefixSpaceable(atomPrefixOrNull)

fun spaced(spaceable: Spaceable) = Spaced(spaceable)

fun body(indent: Indent, spaceable: Spaceable) = Body(indent, spaceable)

fun line(header: Header): Line = HeaderLine(header)
fun line(body: Body): Line = BodyLine(body)
fun line() = line(header())

fun tokens(vararg tokens: Token) = Tokens(stack(*tokens))
fun prefix(tokens: Tokens, line: Line) = TokensPrefix(tokens, line)

fun tokensPrefix() = prefix(tokens(), line())

fun Indent.plus(tab: Tab): Indent = tabStack.push(tab).let(::Indent)
fun Tokens.plus(token: Token): Tokens = tokenStack.push(token).let(::Tokens)
fun Tokens.plus(tokens: Tokens): Tokens = tokenStack.pushAll(tokens.tokenStack).let(::Tokens)

fun TokensPrefix.plusOrNull(string: String): TokensPrefix? =
	orNull.fold(string.charSeq) { this?.plusOrNull(it) }

val Indent.isEmpty: Boolean get() = tabStack.isEmpty
val IndentPrefix.isEmpty: Boolean get() = indent.isEmpty && tabPrefixOrNull == null