package leo.interactive.parser

import leo.base.ifNotNull
import leo.charString
import leo.fold
import leo.reverse
import leo.stack

fun Appendable.append(letter: Letter): Appendable = append(letter.char)
fun Appendable.append(word: Word): Appendable = append(word.string)
fun Appendable.append(digit: Digit): Appendable = append(digit.char)

fun Appendable.append(positiveNumber: PositiveNumber): Appendable =
	fold(stack(positiveNumber.digitStackLink).reverse) { append(it) }

fun Appendable.append(partialNegativeNumber: PartialNegativeNumber): Appendable =
	append('-').ifNotNull(partialNegativeNumber.positiveNumberOrNull) { append(it) }

fun Appendable.append(partialNumber: PartialNumber): Appendable =
	when (partialNumber) {
		is PartialNegativeNumberPartialNumber -> append(partialNumber.partialNegativeNumber)
		is PositiveNumberPartialNumber -> append(partialNumber.positiveNumber)
	}

fun Appendable.append(partialLiteralText: PartialLiteralText): Appendable =
	append('"').append(partialLiteralText.charStack.charString)

fun Appendable.append(partialLiteral: PartialLiteral): Appendable =
	when (partialLiteral) {
		is PartialNumberPartialLiteral -> append(partialLiteral.partialNumber)
		is PartialTextPartialLiteral -> append(partialLiteral.partialText)
	}

fun Appendable.append(partialAtom: PartialAtom): Appendable =
	when (partialAtom) {
		is PartialLiteralPartialAtom -> append(partialAtom.partialLiteral)
		is WordPartialAtom -> append(partialAtom.word)
	}

fun Appendable.append(tab: Tab): Appendable = append("  ")
fun Appendable.append(partialTab: PartialTab): Appendable = append(' ')
fun Appendable.append(endTab: EndTab): Appendable = append(Tab)
fun Appendable.append(indent: Indent): Appendable = fold(indent.endTabStack.reverse) { append(it) }
fun Appendable.append(outdent: Outdent): Appendable = this

fun Appendable.append(body: Body): Appendable =
	ifNotNull(body.partialAtomOrNull) { append(it) }

fun Appendable.append(indentBody: IndentBody): Appendable =
	append(indentBody.indent).append(indentBody.body)

fun Appendable.append(lead: Lead): Appendable = append(lead.indent).append(lead.outdent)

fun Appendable.append(partialLead: PartialLead): Appendable =
	append(partialLead.lead).ifNotNull(partialLead.partialTabOrNull) { append(it) }

fun Appendable.append(partialLine: PartialLine): Appendable =
	when (partialLine) {
		is IndentBodyPartialLine -> append(partialLine.indentBody)
		is PartialLeadPartialLine -> append(partialLine.partialLead)
	}
