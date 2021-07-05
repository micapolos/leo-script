package leo.term.compiler.runtime

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
import leo.fieldTo
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
import leo.term.anyDouble
import leo.term.anyString
import leo.term.native
import leo.term.typed.Typed
import leo.term.typed.TypedValue
import leo.term.typed.typed
import leo.term.value
import leo.term.variable
import leo.type

val TypedValue<Any?>.script: Script get() =
	when (t) {
		is ChoiceType -> typed(v, t.choice).choiceScript
		is StructureType -> typed(v, t.structure).structureScript
	}

val Typed<Value<Any?>, TypeChoice>.choiceScript: Script get() =
	TODO()

val Typed<Value<Any?>, TypeStructure>.structureScript: Script get() =
	t.lineStack.linkOrNull.let { linkOrNull ->
		if (linkOrNull == null) script()
		else
			if (linkOrNull.tail.structure.isStatic)
				if (linkOrNull.head.isStatic)
					typed(value(null), linkOrNull.tail.structure.type).script
						.plus(typed(value(null), linkOrNull.head).scriptLine)
				else
					typed(value(null), linkOrNull.tail.structure.type).script
						.plus(typed(v, linkOrNull.head).scriptLine)
			else
				if (linkOrNull.head.isStatic)
					typed(v, linkOrNull.tail.structure.type).script
						.plus(typed(value(null), linkOrNull.head).scriptLine)
				else
					v.pair.let { (lhs, rhs) ->
						typed(lhs, linkOrNull.tail.structure.type).script
							.plus(typed(rhs, linkOrNull.head).scriptLine)
					}
	}

val Typed<Value<Any?>, TypeLine>.scriptLine: ScriptLine get() =
	typed(v, t.atom).atomScriptLine

val Typed<Value<Any?>, TypeAtom>.atomScriptLine: ScriptLine get() =
	when (t) {
		is FunctionTypeAtom -> typed(v, t.function).functionScriptLine
		is PrimitiveTypeAtom -> typed(v, t.primitive).primitiveScriptLine
	}

val Typed<Value<Any?>, TypeFunction>.functionScriptLine: ScriptLine get() =
	"doing" lineTo script("from" lineTo t.lhsType.script).plus(t.rhsType.script)

val Typed<Value<Any?>, TypePrimitive>.primitiveScriptLine: ScriptLine get() =
	when (t) {
		is FieldTypePrimitive -> line(typed(v, t.field).scriptField)
		is LiteralTypePrimitive -> typed(v, t.literal).literalScriptLine
	}

val Typed<Value<Any?>, TypeLiteral>.literalScriptLine: ScriptLine get() =
	when (t) {
		is NumberTypeLiteral -> line(literal(v.native.anyDouble))
		is TextTypeLiteral -> line(literal(v.native.anyString))
	}

val Typed<Value<Any?>, TypeField>.scriptField: ScriptField get() =
	t.name fieldTo typed(v, t.rhsType).script

val <T> Value<T>.pair: Pair<Value<T>, Value<T>> get() =
	(this as FunctionValue).function.scope.let { scope ->
		scope.value(variable(1)) to scope.value(variable(0))
	}