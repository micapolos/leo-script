package leo.indexed.evaluator

import leo.ChoiceType
import leo.DoingTypeAtom
import leo.FieldTypePrimitive
import leo.LiteralTypePrimitive
import leo.NumberTypeLiteral
import leo.PrimitiveTypeAtom
import leo.Script
import leo.ScriptLine
import leo.StructureType
import leo.TextTypeLiteral
import leo.Type
import leo.TypeAtom
import leo.TypeChoice
import leo.TypeDoing
import leo.TypeField
import leo.TypeLine
import leo.TypeLiteral
import leo.TypeNumber
import leo.TypePrimitive
import leo.TypeStructure
import leo.TypeText
import leo.atom
import leo.doingName
import leo.isEmpty
import leo.line
import leo.lineTo
import leo.literal
import leo.onlyLineOrNull
import leo.plus
import leo.script
import leo.scriptLine
import leo.stack
import leo.zipMapOrNull

@Suppress("unused")
fun TypeNumber.scriptLine(value: Value): ScriptLine =
	line(literal(value.valueDouble))

@Suppress("unused")
fun TypeText.scriptLine(value: Value): ScriptLine =
	line(literal(value.valueString))

fun TypeLiteral.scriptLine(value: Value): ScriptLine =
	when (this) {
		is NumberTypeLiteral -> number.scriptLine(value)
		is TextTypeLiteral -> text.scriptLine(value)
	}

fun TypeField.scriptLine(value: Value): ScriptLine =
	name lineTo rhsType.script(value)

fun TypePrimitive.scriptLine(value: Value): ScriptLine =
	when (this) {
		is FieldTypePrimitive -> field.scriptLine(value)
		is LiteralTypePrimitive -> literal.scriptLine(value)
	}

fun TypeDoing.scriptLine(value: Value): ScriptLine =
	doingName lineTo lhsTypeStructure.script.plus("to" lineTo script(rhsTypeLine.scriptLine))

fun TypeAtom.scriptLine(value: Value): ScriptLine =
	when (this) {
		is DoingTypeAtom -> doing.scriptLine
		is PrimitiveTypeAtom -> primitive.scriptLine(value)
	}

fun TypeLine.scriptLine(value: Value): ScriptLine =
	atom.scriptLine(value)

fun TypeStructure.script(value: Value): Script =
	if (isEmpty) script()
	else onlyLineOrNull
		?.let { it.scriptLine(value).script }
		?: zipMapOrNull(lineStack, stack(*value.valueList.toTypedArray())) { lhs, rhs ->
				lhs.scriptLine(rhs)
			}!!.script

fun TypeChoice.script(value: Value): Script =
	TODO()

fun Type.script(value: Value): Script =
	when (this) {
		is ChoiceType -> choice.script(value)
		is StructureType -> structure.script(value)
	}