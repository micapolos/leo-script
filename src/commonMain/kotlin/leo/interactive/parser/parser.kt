@file:Suppress("unused", "UNUSED_PARAMETER")

package leo.interactive.parser

import leo.Atom
import leo.Literal
import leo.LiteralAtom
import leo.NameAtom
import leo.Stack
import leo.StackLink
import leo.atom
import leo.base.charSeq
import leo.base.fold
import leo.base.ifNull
import leo.base.ifOrNull
import leo.base.notNullIf
import leo.base.orNull
import leo.charString
import leo.flatMap
import leo.fold
import leo.interactive.End
import leo.interactive.NameBegin
import leo.interactive.Token
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

data class Letter(val char: Char)
data class Digit(val int: Int)

data class Word(val letterStackLink: StackLink<Letter>)
data class PositiveNumber(val digitStackLink: StackLink<Digit>)

data class PartialNegativeNumber(val positiveNumberOrNull: PositiveNumber?)

sealed class PartialNumber
data class PartialNegativeNumberPartialNumber(val partialNegativeNumber: PartialNegativeNumber): PartialNumber()
data class PositiveNumberPartialNumber(val positiveNumber: PositiveNumber): PartialNumber()

data class PartialLiteralText(val charStack: Stack<Char>)
data class LiteralText(val string: String)

sealed class PartialLiteral
data class PartialTextPartialLiteral(val partialText: PartialLiteralText): PartialLiteral()
data class PartialNumberPartialLiteral(val partialNumber: PartialNumber): PartialLiteral()

sealed class PartialAtom
data class WordPartialAtom(val word: Word): PartialAtom()
data class PartialLiteralPartialAtom(val partialLiteral: PartialLiteral): PartialAtom()

object Tab
object PartialTab

data class EndTab(val endStackLink: StackLink<End>)
data class Indent(val endTabStack: Stack<EndTab>)
data class Outdent(val endTabStackLink: StackLink<EndTab>)

data class Body(val endTabOrNull: EndTab?, val partialAtomOrNull: PartialAtom?)

data class IndentBody(val indent: Indent, val body: Body)

data class Lead(val indent: Indent, val outdent: Outdent)

data class PartialLead(val lead: Lead, val partialTabOrNull: PartialTab?)

sealed class PartialLine
data class PartialLeadPartialLine(val partialLead: PartialLead): PartialLine()
data class IndentBodyPartialLine(val indentBody: IndentBody): PartialLine()

data class Tokens(val tokenStack: Stack<Token>)
data class PartialTokens(val tokens: Tokens, val partialLine: PartialLine)

val Digit.char: Char get() = int.digitToChar()
val Word.string: String get() = stack(letterStackLink).map { char }.charString

val Int.digitOrNull: Digit? get() = notNullIf(this in 0..9) { Digit(this) }
val Char.digitOrNull: Digit? get() = try { digitToInt().digitOrNull } catch (e: Exception) { null }
val Char.letterOrNull: Letter? get() = notNullIf(isLetter()) { Letter(this) }

val Char.wordOrNull: Word? get() = letterOrNull?.let { Word(stackLink(it)) }
fun Word.plus(letter: Letter): Word = letterStackLink.push(letter).let(::Word)
fun Word.plusOrNull(char: Char): Word? = char.letterOrNull?.let { plus(it) }

val Char.positiveNumberOrNull: PositiveNumber? get() = digitOrNull?.let { PositiveNumber(stackLink(it)) }
fun PositiveNumber.plus(digit: Digit): PositiveNumber = digitStackLink.push(digit).let(::PositiveNumber)
fun PositiveNumber.plusOrNull(char: Char): PositiveNumber? = char.digitOrNull?.let { plus(it) }

val Char.partialNegativeNumberOrNull: PartialNegativeNumber? get() = notNullIf(this == '-') { PartialNegativeNumber(null) }
fun PartialNegativeNumber.plusOrNull(char: Char): PartialNegativeNumber? =
	(if (positiveNumberOrNull == null) char.positiveNumberOrNull
	 else positiveNumberOrNull.plusOrNull(char))?.let(::PartialNegativeNumber)

val Char.partialNumberOrNull: PartialNumber? get() =
	null
		?: partialNegativeNumberOrNull?.let(::PartialNegativeNumberPartialNumber)
		?: positiveNumberOrNull?.let(::PositiveNumberPartialNumber)

fun PartialNumber.plusOrNull(char: Char): PartialNumber? =
	when (this) {
		is PartialNegativeNumberPartialNumber -> partialNegativeNumber.plusOrNull(char)?.let(::PartialNegativeNumberPartialNumber)
		is PositiveNumberPartialNumber -> positiveNumber.plusOrNull(char)?.let(::PositiveNumberPartialNumber)
	}

val PartialNegativeNumber.doubleOrNull: Double? get() =
	positiveNumberOrNull?.double?.let { -it }

val PositiveNumber.double: Double get() =
	0.0.fold(stack(digitStackLink)) { times(10).plus(it.int) }

val PartialNumber.doubleOrNull: Double? get() =
	when (this) {
		is PartialNegativeNumberPartialNumber -> partialNegativeNumber.doubleOrNull
		is PositiveNumberPartialNumber -> positiveNumber.double
	}

val End.tab: EndTab get() = EndTab(stackLink(this))
fun EndTab.plus(end: End): EndTab = endStackLink.push(end).let(::EndTab)

val Char.partialTextOrNull: PartialLiteralText? get() = notNullIf(this == '\"') { PartialLiteralText(stack()) }

fun PartialLiteralText.plus(char: Char) = charStack.push(char).let(::PartialLiteralText)
fun PartialLiteralText.plusTextOrNull(char: Char): LiteralText? = notNullIf(char == '\"') { literalText }
val PartialLiteralText.literalText: LiteralText get() = charStack.charString.let(::LiteralText)

fun PartialLiteral.plusOrNull(char: Char): PartialLiteral? =
	when (this) {
		is PartialNumberPartialLiteral -> partialNumber.plusOrNull(char)?.let(::PartialNumberPartialLiteral)
		is PartialTextPartialLiteral -> partialText.plus(char).let(::PartialTextPartialLiteral)
	}

fun PartialLiteral.plusLiteralOrNull(char: Char): Literal? =
	when (this) {
		is PartialNumberPartialLiteral -> partialNumber.doubleOrNull?.let { it.literal }
		is PartialTextPartialLiteral -> partialText.plusTextOrNull(char)?.let { it.string.literal }
	}

val Char.partialLiteralOrNull: PartialLiteral? get() =
	null
		?: partialNumberOrNull?.let(::PartialNumberPartialLiteral)
		?: partialTextOrNull?.let(::PartialTextPartialLiteral)

val Char.partialAtomOrNull: PartialAtom? get() =
	null
		?: wordOrNull?.let(::WordPartialAtom)
		?: partialLiteralOrNull?.let(::PartialLiteralPartialAtom)

fun PartialAtom.plusPartialTokensOrNull(char: Char): PartialTokens? =
	when (char) {
		'.' -> atomOrNull?.dotTokens?.let { PartialTokens(it, emptyPartialLine) }
		' ' -> atomOrNull?.beginTokensOrNull?.let { PartialTokens(it, emptyPartialLine) }
		'\n' -> atomOrNull?.beginTokensOrNull?.let { PartialTokens(it, emptyPartialLine) }
		else -> null
	}

val PartialLine.beginOrNull: NameBegin? get() =
	when (this) {
		is IndentBodyPartialLine -> indentBody.beginOrNull
		is PartialLeadPartialLine -> null
	}

val PartialLine.tokensOrNull: Tokens? get() =
	when (this) {
		is IndentBodyPartialLine -> indentBody.tokensOrNull
		is PartialLeadPartialLine -> null
	}

val IndentBody.beginOrNull: NameBegin? get() = body.beginOrNull
val IndentBody.tokensOrNull: Tokens? get() = body.tokensOrNull

val Body.beginOrNull: NameBegin? get() =
	partialAtomOrNull?.beginOrNull

val Body.tokensOrNull: Tokens? get() =
	partialAtomOrNull?.tokensOrNull

val PartialAtom.beginOrNull: NameBegin? get() =
	when (this) {
		is PartialLiteralPartialAtom -> null
		is WordPartialAtom -> begin(word.string)
	}

val PartialAtom.tokensOrNull: Tokens? get() =
	when (this) {
		is PartialLiteralPartialAtom -> null
		is WordPartialAtom -> Tokens(stack(token(begin(word.string)), token(end)))
	}

val Atom.beginTokensOrNull: Tokens? get() =
	beginOrNull?.let { Tokens(stack(token(it))) }

val Atom.beginOrNull: NameBegin? get() =
	when (this) {
		is LiteralAtom -> null
		is NameAtom -> begin(name)
	}

val Atom.dotTokens: Tokens get() =
	when (this) {
		is LiteralAtom -> Tokens(stack(token(literal)))
		is NameAtom -> Tokens(stack(token(begin(name)), token(end)))
	}

val PartialAtom.atomOrNull: Atom? get() =
	when (this) {
		is PartialLiteralPartialAtom -> null
		is WordPartialAtom -> atom(word.string)
	}

val Char.partialTabOrNull: PartialTab? get() = notNullIf(this == ' ') { PartialTab }

fun PartialAtom.plusAtomOrNull(char: Char): Atom? =
	when (this) {
		is PartialLiteralPartialAtom -> partialLiteral.plusLiteralOrNull(char)?.let(::atom)
		is WordPartialAtom -> word.plusOrNull(char)?.string?.let(::atom)
	}

fun PartialAtom.plusOrNull(char: Char): PartialAtom? =
	when (this) {
		is PartialLiteralPartialAtom -> partialLiteral.plusOrNull(char)?.let(::PartialLiteralPartialAtom)
		is WordPartialAtom -> word.plusOrNull(char)?.let(::WordPartialAtom)
	}

fun PartialTab.plusOrNull(char: Char): PartialTab? = null
fun PartialTab.plusTabOrNull(char: Char): Tab? = notNullIf(char == ' ') { Tab }

val EndTab.endStack: Stack<End> get() = stack(endStackLink)
val Outdent.endStack: Stack<End> get() = endStack.flatMap { endStack }
val Lead.endStack: Stack<End> get() = outdent.endStack

val emptyIndent: Indent get() = Indent(stack())
val emptyBody: Body get() = Body(null, null)

fun Lead.plusPartialLeadOrNull(char: Char): PartialLead? =
	char.partialTabOrNull?.let { PartialLead(this, it) }

fun Indent.plus(endTab: EndTab): Indent = endTabStack.push(endTab).let(::Indent)
val Lead.shiftOrNull: Lead? get() = outdent.endTabStackLink.tail.linkOrNull?.let {
	Lead(indent.plus(outdent.endTabStackLink.head), Outdent(it))
}

fun PartialLead.plusOrNull(char: Char): PartialLead? =
	if (partialTabOrNull == null) char.partialTabOrNull?.let { PartialLead(lead, it) }
	else partialTabOrNull.plusTabOrNull(char)?.let {
		lead.shiftOrNull?.let { PartialLead(it, null) }
	}

fun PartialLead.plusLeadOrNull(char: Char): Lead? =
	if (partialTabOrNull == null) null
	else partialTabOrNull.plusTabOrNull(char)?.let { lead.shiftOrNull }

fun PartialLead.plusUnitOrNull(char: Char): Unit? =
	if (partialTabOrNull == null) null
	else partialTabOrNull.plusTabOrNull(char)?.let {
		lead.shiftOrNull.ifNull { Unit }
	}

fun Indent.plusIndentBodyOrNull(char: Char): IndentBody? =
	char.bodyOrNull?.let { IndentBody(this, it) }

val Char.bodyOrNull: Body? get() =
	partialAtomOrNull?.let { Body(null, it) }

fun Body.plusOrNull(char: Char): Body? =
	(if (partialAtomOrNull == null) char.partialAtomOrNull
	 else partialAtomOrNull.plusOrNull(char))
		?.let { Body(endTabOrNull, it) }

val Body.plusSpaceOrNull: Body? get() =
	partialAtomOrNull?.beginOrNull?.let {
		Body(endTabOrNull?.plus(end)?:end.tab, null)
	}

val Body.plusNewlineOrNull: Body? get() =
	partialAtomOrNull?.beginOrNull?.let {
		Body(null, null)
	}

val Body.plusDotOrNull: Body? get() =
	Body(endTabOrNull, null)

val IndentBody.plusSpaceOrNull: IndentBody? get() =
	body.plusSpaceOrNull?.let { IndentBody(indent, it) }

val IndentBody.plusNewlineOrNull: IndentBody? get() =
	body.plusNewlineOrNull?.let { IndentBody(emptyIndent, it) }

val IndentBody.plusDotOrNull: IndentBody? get() =
	body.plusDotOrNull?.let { IndentBody(indent, it) }

val PartialLine.plusSpaceOrNull: PartialLine? get() =
	when (this) {
		is IndentBodyPartialLine -> indentBody.plusSpaceOrNull?.let(::IndentBodyPartialLine)
		is PartialLeadPartialLine -> null
	}

val PartialLine.plusNewlineOrNull: PartialLine? get() =
	when (this) {
		is IndentBodyPartialLine -> indentBody.plusNewlineOrNull?.let(::IndentBodyPartialLine)
		is PartialLeadPartialLine -> null
	}

val PartialLine.plusDotOrNull: PartialLine? get() =
	when (this) {
		is IndentBodyPartialLine -> indentBody.plusDotOrNull?.let(::IndentBodyPartialLine)
		is PartialLeadPartialLine -> null
	}

fun IndentBody.plusOrNull(char: Char): IndentBody? =
	body.plusOrNull(char)?.let { IndentBody(indent, it) }

fun PartialLine.plusOrNull(char: Char): PartialLine? =
	when (this) {
		is IndentBodyPartialLine -> indentBody.plusOrNull(char)?.let(::IndentBodyPartialLine)
		is PartialLeadPartialLine -> partialLead.plusOrNull(char)?.let(::PartialLeadPartialLine)
	}

val emptyIndentBody: IndentBody get() = IndentBody(emptyIndent, emptyBody)
val emptyPartialLine: PartialLine get() = emptyIndentBody.let(::IndentBodyPartialLine)

fun Body.plusPartialTokensOrNull(char: Char): PartialTokens? =
	partialAtomOrNull?.plusPartialTokensOrNull(char)

fun IndentBody.plusPartialTokensOrNull(char: Char): PartialTokens? =
	body.plusPartialTokensOrNull(char)

fun PartialLine.plusPartialTokensOrNull(char: Char): PartialTokens? =
	when (this) {
		is IndentBodyPartialLine -> indentBody.plusPartialTokensOrNull(char)
		is PartialLeadPartialLine -> partialLead.plusUnitOrNull(char)?.let { PartialTokens(emptyTokens, emptyPartialLine) }
	}

val emptyTokens: Tokens = Tokens(stack())
fun Tokens.plus(token: Token): Tokens = tokenStack.push(token).let(::Tokens)
fun Tokens.plus(tokens: Tokens): Tokens = fold(tokens.tokenStack.reverse) { plus(it) }

val emptyPartialTokens: PartialTokens get() =
	PartialTokens(emptyTokens, emptyPartialLine)

fun Tokens.plusPartialTokens(partialTokens: PartialTokens): PartialTokens =
	PartialTokens(plus(partialTokens.tokens), partialTokens.partialLine)

fun PartialTokens.plusOrNull(char: Char): PartialTokens? =
	when (char) {
		' ' -> plusSpaceOrNull
		'\n' -> plusNewlineOrNull
		'.' -> plusDotOrNull
		else -> partialLine.plusOrNull(char)?.let { PartialTokens(tokens, it) }
	}

val PartialTokens.plusSpaceOrNull: PartialTokens? get() =
	partialLine.beginOrNull?.let { begin ->
		partialLine.plusSpaceOrNull?.let { partialLine ->
			PartialTokens(tokens.plus(token(begin)), partialLine)
		}
	}

val PartialTokens.plusNewlineOrNull: PartialTokens? get() =
	partialLine.beginOrNull?.let { begin ->
		partialLine.plusNewlineOrNull?.let { partialLine ->
			PartialTokens(tokens.plus(token(begin)), partialLine)
		}
	}

val PartialTokens.plusDotOrNull: PartialTokens? get() =
	partialLine.tokensOrNull?.let { partialLineTokens ->
		partialLine.plusDotOrNull?.let { partialLine ->
			PartialTokens(tokens.plus(partialLineTokens), partialLine)
		}
	}

val PartialLine.indentBodyOrNull: IndentBody? get() = (this as? IndentBodyPartialLine)?.indentBody

val PartialLine.isEmpty: Boolean get() = indentBodyOrNull?.body?.isEmpty?:false
val Body.isEmpty: Boolean get() = partialAtomOrNull == null

val PartialTokens.completeTokensOrNull: Tokens? get() =
	ifOrNull(partialLine.isEmpty) { tokens }

fun PartialTokens.plusOrNull(string: String): PartialTokens? =
	orNull.fold(string.charSeq) { this?.plusOrNull(it) }
