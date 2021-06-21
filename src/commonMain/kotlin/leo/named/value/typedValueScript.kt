package leo.named.value

import leo.FieldTypePrimitive
import leo.FunctionTypeAtom
import leo.LiteralTypePrimitive
import leo.NumberTypeLiteral
import leo.PrimitiveTypeAtom
import leo.Script
import leo.ScriptLine
import leo.TextTypeLiteral
import leo.Type
import leo.TypeAtom
import leo.TypeField
import leo.TypeFunction
import leo.TypeLine
import leo.TypeLiteral
import leo.TypePrimitive
import leo.atom
import leo.functionName
import leo.givingName
import leo.line
import leo.lineTo
import leo.literal
import leo.map
import leo.named.typed.Typed
import leo.script
import leo.takingName

val Typed<Value, Type>.scriptLine: ScriptLine
	get() =
		"value" lineTo script

val Typed<Value, Type>.script: Script
	get() =
		lineStack.map { lineScriptLine }.script

val Typed<ValueLine, TypeLine>.lineScriptLine: ScriptLine
	get() =
		Typed(v, t.atom).atomScriptLine

val Typed<ValueLine, TypeAtom>.atomScriptLine: ScriptLine
	get() =
		when (t) {
			is FunctionTypeAtom -> Typed(v.functionOrNull!!, t.function).functionScriptLine
			is PrimitiveTypeAtom -> Typed(v, t.primitive).primitiveScriptLine
		}

val Typed<ValueLine, TypePrimitive>.primitiveScriptLine: ScriptLine
	get() =
		when (t) {
			is FieldTypePrimitive -> Typed(v.fieldOrNull!!, t.field).fieldScriptLine
			is LiteralTypePrimitive -> Typed(v.anyOrNull, t.literal).literalScriptLine
		}

val Typed<Any?, TypeLiteral>.literalScriptLine: ScriptLine get() =
	when (t) {
		is NumberTypeLiteral -> line(literal((v as Double)))
		is TextTypeLiteral -> line(literal((v as String)))
	}

val Typed<ValueField, TypeField>.fieldScriptLine: ScriptLine
	get() =
	  t.name lineTo rhs.script

val Typed<ValueFunction, TypeFunction>.functionScriptLine: ScriptLine get() =
	functionName lineTo script(
		takingName lineTo t.lhsType.script,
		givingName lineTo t.rhsType.script)
