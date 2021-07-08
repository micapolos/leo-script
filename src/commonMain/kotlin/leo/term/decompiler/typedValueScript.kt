package leo.term.decompiler

import leo.ChoiceType
import leo.FieldTypePrimitive
import leo.FunctionTypeAtom
import leo.LiteralTypePrimitive
import leo.NumberTypeLiteral
import leo.PrimitiveTypeAtom
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.StructureType
import leo.TextTypeLiteral
import leo.TypeAtom
import leo.TypeChoice
import leo.TypeField
import leo.TypeFunction
import leo.TypeLine
import leo.TypeLiteral
import leo.TypePrimitive
import leo.TypeStructure
import leo.atom
import leo.base.Conditional
import leo.base.stak.top
import leo.fieldTo
import leo.isEmpty
import leo.isStatic
import leo.line
import leo.lineTo
import leo.linkOrNull
import leo.literal
import leo.plus
import leo.script
import leo.structure
import leo.term.FunctionValue
import leo.term.Value
import leo.term.abstractionOrNull
import leo.term.applicationOrNull
import leo.term.compiler.native.Native
import leo.term.compiler.native.double
import leo.term.compiler.native.string
import leo.term.functionOrNull
import leo.term.idValue
import leo.term.native
import leo.term.typed.Typed
import leo.term.typed.TypedValue
import leo.term.typed.typed
import leo.term.value
import leo.term.variable
import leo.term.variableOrNull
import leo.type

val TypedValue<Native>.script: Script
  get() =
    when (t) {
      is ChoiceType -> typed(v, t.choice).choiceScript
      is StructureType -> typed(v, t.structure).structureScript
    }

val Typed<Value<Native>, TypeChoice>.choiceScript: Script
  get() =
    t.lineStack.linkOrNull.let { linkOrNull ->
      if (linkOrNull == null) error("impossible")
      else if (linkOrNull.tail.isEmpty) script(typed(v, linkOrNull.head).scriptLine)
      else v.eitherConditional.let { conditional ->
        if (conditional.boolean) script(typed(conditional.v, linkOrNull.head).scriptLine)
        else typed(conditional.v, TypeChoice(linkOrNull.tail)).choiceScript
      }
    }

val Typed<Value<Native>, TypeStructure>.structureScript: Script
  get() =
    t.lineStack.linkOrNull.let { linkOrNull ->
      if (linkOrNull == null) script()
      else
        if (linkOrNull.tail.structure.isStatic)
          if (linkOrNull.head.isStatic)
            typed(idValue<Native>(), linkOrNull.tail.structure.type).script
              .plus(typed(idValue<Native>(), linkOrNull.head).scriptLine)
          else
            typed(idValue<Native>(), linkOrNull.tail.structure.type).script
              .plus(typed(v, linkOrNull.head).scriptLine)
        else
          if (linkOrNull.head.isStatic)
            typed(v, linkOrNull.tail.structure.type).script
              .plus(typed(idValue<Native>(), linkOrNull.head).scriptLine)
          else
            v.pair.let { (lhs, rhs) ->
              typed(lhs, linkOrNull.tail.structure.type).script
                .plus(typed(rhs, linkOrNull.head).scriptLine)
            }
    }

val Typed<Value<Native>, TypeLine>.scriptLine: ScriptLine
  get() =
    typed(v, t.atom).atomScriptLine

val Typed<Value<Native>, TypeAtom>.atomScriptLine: ScriptLine
  get() =
    when (t) {
      is FunctionTypeAtom -> typed(v, t.function).functionScriptLine
      is PrimitiveTypeAtom -> typed(v, t.primitive).primitiveScriptLine
    }

val Typed<Value<Native>, TypeFunction>.functionScriptLine: ScriptLine
  get() =
    "doing" lineTo script("from" lineTo t.lhsType.script).plus(t.rhsType.script)

val Typed<Value<Native>, TypePrimitive>.primitiveScriptLine: ScriptLine
  get() =
    when (t) {
      is FieldTypePrimitive -> line(typed(v, t.field).scriptField)
      is LiteralTypePrimitive -> typed(v, t.literal).literalScriptLine
    }

val Typed<Value<Native>, TypeLiteral>.literalScriptLine: ScriptLine
  get() =
    when (t) {
      is NumberTypeLiteral -> line(literal(v.native.double))
      is TextTypeLiteral -> line(literal(v.native.string))
    }

val Typed<Value<Native>, TypeField>.scriptField: ScriptField
  get() =
    t.name fieldTo typed(v, t.rhsType).script

val <T> Value<T>.pair: Pair<Value<T>, Value<T>>
  get() =
    (this as FunctionValue).function.scope.let { scope ->
      scope.value(variable(1)) to scope.value(variable(0))
    }

val <T> Value<T>.eitherConditional: Conditional<Value<T>>
  get() =
    functionOrNull!!.let { function ->
      function.term.abstractionOrNull!!.term.applicationOrNull!!.lhs.variableOrNull!!.index.let { index ->
        Conditional(index == 0, function.scope.valueStak.top!!)
      }
    }