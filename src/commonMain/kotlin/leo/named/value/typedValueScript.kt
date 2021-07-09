package leo.named.value

import leo.AnyTypePrimitive
import leo.FieldTypePrimitive
import leo.FunctionTypeAtom
import leo.PrimitiveTypeAtom
import leo.Script
import leo.ScriptLine
import leo.Type
import leo.TypeAny
import leo.TypeAtom
import leo.TypeField
import leo.TypeFunction
import leo.TypeLine
import leo.TypePrimitive
import leo.any
import leo.atom
import leo.base.runIf
import leo.functionName
import leo.givingName
import leo.line
import leo.lineTo
import leo.literal
import leo.map
import leo.named.typed.Typed
import leo.nativeName
import leo.numberName
import leo.script
import leo.takingName
import leo.textName
import leo.theName

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
      is AnyTypePrimitive -> Typed(v.anyOrNull, t.any).anyScriptLine
    }

val Typed<Any?, TypeAny>.anyScriptLine: ScriptLine
  get() =
    when (t) {
      any(numberName) -> line(literal((v as Double)))
      any(textName) -> line(literal((v as String)))
      else -> nativeName lineTo script(literal("$v"))
    }

val Typed<ValueField, TypeField>.fieldScriptLine: ScriptLine
  get() =
    unescapedFieldScriptLine.runIf(t.name.isValueKeyword) { theName lineTo script(this) }

val Typed<ValueField, TypeField>.unescapedFieldScriptLine: ScriptLine
  get() =
    t.name lineTo rhs.script

val Typed<ValueFunction, TypeFunction>.functionScriptLine: ScriptLine
  get() =
    functionName lineTo script(
      takingName lineTo t.lhsType.script,
      givingName lineTo t.rhsType.script
    )

val String.isValueKeyword: Boolean
  get() =
    when (this) {
      functionName -> true
      theName -> true
      else -> false
    }