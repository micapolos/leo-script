package leo.interactive.parser

import leo.Stack
import leo.StackLink
import leo.base.notNullIf
import leo.charString
import leo.push
import leo.stack
import leo.stackLink

data class Letter(val letterChar: Char)
data class Digit(val int: Int)

data class Word(val letterStackLink: StackLink<Letter>)
data class PositiveNumber(val digitStackLink: StackLink<Digit>)

data class PartialNegativeNumber(val positiveNumberOrNull: PositiveNumber?)

sealed class PartialNumber
data class PartialNegativeNumberPartialNumber(val partialNegativeNumber: PartialNegativeNumber): PartialNumber()
data class PositiveNumberPartialNumber(val positiveNumber: PositiveNumber): PartialNumber()

data class PartialLiteralText(val charStack: Stack<Char>)
data class Text(val string: String)

sealed class PartialLiteral
data class PartialTextPartialLiteral(val partialText: PartialLiteralText): PartialLiteral()
data class PartialNumberPartialLiteral(val partialNumber: PartialNumber): PartialLiteral()

sealed class PartialAtom
data class WordPartialAtom(val word: Word): PartialAtom()
data class PartialLiteralPartialAtom(val partialLiteral: PartialLiteral): PartialAtom()

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

val Char.partialTextOrNull: PartialLiteralText? get() = notNullIf(this == '\"') { PartialLiteralText(stack()) }

fun PartialLiteralText.plus(char: Char) = charStack.push(char).let(::PartialLiteralText)
fun PartialLiteralText.plusTextOrNull(char: Char): Text? = notNullIf(char == '\"') { text }
val PartialLiteralText.text: Text get() = charStack.charString.let(::Text)

fun PartialLiteral.plusOrNull(char: Char): PartialLiteral? =
	when (this) {
		is PartialNumberPartialLiteral -> partialNumber.plusOrNull(char)?.let(::PartialNumberPartialLiteral)
		is PartialTextPartialLiteral -> partialText.plus(char).let(::PartialTextPartialLiteral)
	}

val Char.partialLiteralOrNull: PartialLiteral? get() =
	null
		?: partialNumberOrNull?.let(::PartialNumberPartialLiteral)
		?: partialTextOrNull?.let(::PartialTextPartialLiteral)

val Char.partialAtomOrNull: PartialAtom? get() =
	null
		?: wordOrNull?.let(::WordPartialAtom)
		?: partialLiteralOrNull?.let(::PartialLiteralPartialAtom)

fun PartialAtom.plusOrNull(char: Char): PartialAtom? =
	when (this) {
		is PartialLiteralPartialAtom -> partialLiteral.plusOrNull(char)?.let(::PartialLiteralPartialAtom)
		is WordPartialAtom -> word.plusOrNull(char)?.let(::WordPartialAtom)
	}

