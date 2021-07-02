package leo.interactive.parser

import leo.Atom
import leo.Literal
import leo.LiteralAtom
import leo.NameAtom
import leo.atom
import leo.base.ifNotNull
import leo.base.notNullIf
import leo.charString
import leo.fold
import leo.interactive.End
import leo.interactive.begin
import leo.interactive.end
import leo.interactive.token
import leo.linkOrNull
import leo.literal
import leo.map
import leo.push
import leo.reverse
import leo.stack
import leo.stackLink


val Digit.char: Char get() = int.digitToChar()
val Word.string: String get() = stack(letterStackLink).map { char }.charString

val Int.digitOrNull: Digit? get() = notNullIf(this in 0..9) { Digit(this) }
val Char.digitOrNull: Digit? get() = try { digitToInt().digitOrNull } catch (e: Exception) { null }
val Char.letterOrNull: Letter? get() = notNullIf(isLetter()) { Letter(this) }

val Char.wordOrNull: Word? get() = letterOrNull?.let { Word(stackLink(it)) }
fun Word.plus(letter: Letter): Word = letterStackLink.push(letter).let(::Word)
fun Word.plusOrNull(char: Char): Word? = char.letterOrNull?.let { plus(it) }

val Char.numberOrNull: Number? get() = digitOrNull?.let { Number(stackLink(it)) }
fun Number.plus(digit: Digit): Number = digitStackLink.push(digit).let(::Number)
fun Number.plusOrNull(char: Char): Number? = char.digitOrNull?.let { plus(it) }

val Char.numberNegativeOrNull: NumberNegative? get() = notNullIf(this == '-') { NumberNegative(null) }
fun NumberNegative.plusOrNull(char: Char): NumberNegative? =
	(if (numberOrNull == null) char.numberOrNull
	else numberOrNull.plusOrNull(char))?.let(::NumberNegative)

val Char.numberPrefixOrNull: NumberPrefix? get() =
	null
		?: numberNegativeOrNull?.let(::NegativeNumberPrefix)
		?: numberOrNull?.let(::NumberNumberPrefix)

fun NumberPrefix.plusOrNull(char: Char): NumberPrefix? =
	when (this) {
		is NegativeNumberPrefix -> negative.plusOrNull(char)?.let(::NegativeNumberPrefix)
		is NumberNumberPrefix -> number.plusOrNull(char)?.let(::NumberNumberPrefix)
	}

val NumberNegative.literalOrNull: Literal? get() =
	numberOrNull?.double?.let { literal(-it) }

val Number.double: Double get() =
	0.0.fold(stack(digitStackLink).reverse) { times(10).plus(it.int) }

val NumberPrefix.literalOrNull: Literal? get() =
	when (this) {
		is NegativeNumberPrefix -> negative.literalOrNull
		is NumberNumberPrefix -> number.double.literal
	}

fun Tab.plus(end: End): Tab = endStackLink.push(end).let(::Tab)

val Char.textOpeningOrNull: TextOpening? get() =
	notNullIf(this == '\"') { TextOpening(stack(), null) }

val Char.escapeOrNull: Escape? get() = notNullIf(this == '\\') { escape }
val Char.textItemPrefixOrNull: TextItemPrefix? get() = escapeOrNull?.let { textItemPrefix(it) }

fun TextItemPrefix.plusItemOrNull(char: Char): TextItem? =
	EscapedTextItem(CharEscaped(char))

fun TextOpening.plus(char: Char) =
	if (itemPrefixOrNull == null) char.textItemPrefixOrNull?.let { TextOpening(itemStack, it) }
	else itemPrefixOrNull.plusItemOrNull(char)?.let { TextOpening(itemStack.push(it), null) }

fun TextOpening.plusLiteralOrNull(char: Char): Literal? =
	plusPrefixOrNull(char)?.literalOrNull

val CharEscaped.char: Char get() =
	when (char) {
		'n' -> '\n'
		'\\' -> '\\'
		else -> char
	}

val TextItem.char: Char get() =
	when (this) {
		is CharTextItem -> char
		is EscapedTextItem -> escaped.char
	}

val TextOpening.stringOrNull: String? get() =
	notNullIf(itemPrefixOrNull == null) {
		itemStack.map { char }.charString
	}

val Char.textItemOrNull: TextItem? get() =
	when (this) {
		'\\' -> null
		'\n' -> null
		else -> textItem(this)
	}

fun TextOpening.plusOrNull(char: Char): TextOpening? =
	if (itemPrefixOrNull == null)
		null
			?: char.textItemOrNull?.let { TextOpening(itemStack.push(it), null) }
			?: char.textItemPrefixOrNull?.let { TextOpening(itemStack, it) }
	else itemPrefixOrNull.plusItemOrNull(char)?.let { TextOpening(itemStack.push(it), null) }

fun TextOpening.plusPrefixOrNull(char: Char): TextPrefix? =
	if (char == '\"') stringOrNull?.let(::StringTextPrefix)
	else plusOrNull(char)?.let { OpeningTextPrefix(it) }

fun TextPrefix.plusOrNull(char: Char): TextPrefix? =
	when (this) {
		is OpeningTextPrefix -> opening.plusPrefixOrNull(char)
		is StringTextPrefix -> null
	}

val TextPrefix.literalOrNull: Literal? get() =
	when (this) {
		is OpeningTextPrefix -> null
		is StringTextPrefix -> string.literal
	}

fun LiteralPrefix.plusOrNull(char: Char): LiteralPrefix? =
	when (this) {
		is NumberLiteralPrefix -> numberPrefix.plusOrNull(char)?.let(::NumberLiteralPrefix)
		is TextLiteralPrefix -> textPrefix.plusOrNull(char)?.let(::TextLiteralPrefix)
	}

fun LiteralPrefix.plusLiteralOrNull(char: Char): Literal? =
	when (this) {
		is NumberLiteralPrefix -> numberPrefix.literalOrNull
		is TextLiteralPrefix -> textPrefix.plusOrNull(char)?.literalOrNull
	}

val Char.textPrefixOrNull: TextPrefix? get() =
	textOpeningOrNull?.let(::OpeningTextPrefix)

val Char.literalPrefixOrNull: LiteralPrefix? get() =
	null
		?: numberPrefixOrNull?.let(::NumberLiteralPrefix)
		?: textPrefixOrNull?.let(::TextLiteralPrefix)

val Char.atomPrefixOrNull: AtomPrefix? get() =
	null
		?: wordOrNull?.let(::WordAtomPrefix)
		?: literalPrefixOrNull?.let(::LiteralAtomPrefix)

val Atom.tokens: Tokens get() =
	when (this) {
		is LiteralAtom -> tokens(token(literal))
		is NameAtom -> tokens(token(begin(name)), token(end))
	}

fun AtomPrefix.plusOrNull(char: Char): AtomPrefix? =
	when (this) {
		is LiteralAtomPrefix -> literalPrefix.plusOrNull(char)?.let(::LiteralAtomPrefix)
		is WordAtomPrefix -> word.plusOrNull(char)?.let(::WordAtomPrefix)
	}

val AtomPrefix.literalPrefixOrNull: LiteralPrefix? get() =
	(this as? LiteralAtomPrefix)?.literalPrefix

val AtomPrefix.wordOrNull: Word? get() =
	(this as? WordAtomPrefix)?.word

val LiteralPrefix.literalOrNull: Literal? get() =
	when (this) {
		is NumberLiteralPrefix -> numberPrefix.literalOrNull
		is TextLiteralPrefix -> textPrefix.literalOrNull
	}

val LiteralPrefix.atomOrNull: Atom? get() =
	literalOrNull?.let(::atom)

val AtomPrefix.atomOrNull: Atom? get() =
	when (this) {
		is LiteralAtomPrefix -> literalPrefix.atomOrNull
		is WordAtomPrefix -> atom(word.string)
	}

fun AtomPrefix.plusAtomOrNull(char: Char): Atom? =
	when (this) {
		is LiteralAtomPrefix -> literalPrefix.plusLiteralOrNull(char)?.let(::atom)
		is WordAtomPrefix -> word.plusOrNull(char)?.string?.let(::atom)
	}

fun Tab.plusPrefixOrNull(char: Char): TabPrefix? =
	notNullIf(char == ' ') { prefix(this) }

fun TabPrefix.plusTabOrNull(char: Char): Tab? =
	notNullIf(char == ' ') { tab }

fun IndentPrefix.plusOrNull(char: Char): IndentPrefix? =
	tabPrefixOrNull?.plusTabOrNull(char)?.let { prefix(indent.plus(it), null) }

val Tab.tokens: Tokens get() =
	stack(endStackLink).map { token(this) }.let(::Tokens)

val IndentSuffix.tokens: Tokens get() =
	tokens().fold(indent.tabStack) { plus(it.tokens) }

fun Header.plusOrNull(char: Char): Header? =
	null
		?: prefix.plusOrNull(char)?.let { header(it, suffix) }
		?: suffix.indent.tabStack.linkOrNull?.let { tabStackLink ->
			tabStackLink.head.plusPrefixOrNull(char)?.let {
				header(prefix(prefix.indent, it), suffix(tabStackLink.tail.let(::Indent)))
			}
		}

fun Header.plusTokensPrefixOrNull(char: Char): TokensPrefix? =
	null
		?: plusOrNull(char)?.let { prefix(tokens(), line(it)) }
		?: char.atomPrefixOrNull?.let {
			prefix(
				suffix.tokens,
				line(body(prefix.indent, spaced(null, it))))
		}

fun Spaced.plusOrNull(char: Char): Spaced? =
	if (atomPrefixOrNull == null) char.atomPrefixOrNull?.let { spaced(tabOrNull, it) }
	else atomPrefixOrNull.plusOrNull(char)?.let { spaced(tabOrNull, it) }

val Spaced.beginTab: Tab get() =
	tabOrNull?.plus(end)?:tab(end)

fun Body.plusTokensPrefixOrNull(char: Char): TokensPrefix? =
	when (char) {
		' ' ->
			spaced.atomPrefixOrNull?.wordOrNull?.string?.let { name ->
				prefix(
					tokens(token(begin(name))),
					line(body(indent, spaced(spaced.beginTab, null))))
			}
		'.' ->
			spaced.atomPrefixOrNull?.atomOrNull?.let { atom ->
				prefix(
					atom.tokens,
					line(body(indent, spaced(spaced.tabOrNull, null))))
			}
		else ->
			spaced
				.plusOrNull(char)
				?.let { prefix(tokens(), line(body(indent, it))) }
	}

fun Line.plusTokensPrefixOrNull(char: Char): TokensPrefix? =
	when (char) {
		'\n' ->
			when (this) {
				is BodyLine -> body.spaced.atomPrefixOrNull?.atomOrNull?.let { atom ->
					when (atom) {
						is LiteralAtom ->
							prefix(
								tokens(token(atom.literal)),
								line(header(prefix(indent(), null), suffix(body.indent.ifNotNull(body.spaced.tabOrNull) { plus(it) } ))))
						is NameAtom ->
							prefix(
								tokens(token(begin(atom.name))),
								line(header(prefix(indent(), null), suffix(body.indent.plus(body.spaced.beginTab)))))
					}
				}
				is HeaderLine -> null
			}
		else ->
			when (this) {
				is BodyLine -> body.plusTokensPrefixOrNull(char)
				is HeaderLine -> header.plusTokensPrefixOrNull(char)
			}
	}

fun TokensPrefix.plusOrNull(char: Char): TokensPrefix? =
	line.plusTokensPrefixOrNull(char)?.let {
		prefix(tokens.plus(it.tokens), it.line)
	}

val Header.endTokensOrNull: Tokens? get() =
	notNullIf(prefix.isEmpty) { suffix.tokens }

val Line.endTokensOrNull: Tokens? get() =
	when (this) {
		is BodyLine -> null
		is HeaderLine -> header.endTokensOrNull
	}

val TokensPrefix.endTokensOrNull: Tokens? get() =
	line.endTokensOrNull?.let { tokens.plus(it) }