package leo.typed.indexed

import leo.ChoiceType
import leo.Empty
import leo.FieldTypePrimitive
import leo.FunctionTypeAtom
import leo.NativeTypePrimitive
import leo.PrimitiveTypeAtom
import leo.Script
import leo.ScriptLine
import leo.StructureType
import leo.Type
import leo.TypeAtom
import leo.TypeChoice
import leo.TypeField
import leo.TypeFunction
import leo.TypeLine
import leo.TypeNative
import leo.TypePrimitive
import leo.TypeStructure
import leo.atom
import leo.empty
import leo.fieldOrNull
import leo.getFromBottom
import leo.isSimple
import leo.lineTo
import leo.map
import leo.mapIt
import leo.onlyLineOrNull
import leo.primitiveOrNull
import leo.script
import leo.scriptLine
import leo.size
import leo.stack
import leo.structureOrNull
import leo.zip

data class ValueScriptContext<in V>(
  val nativeScriptLineFn: (V) -> ScriptLine,
  val customScriptLineOrNullFn: (Value<V>, TypeLine) -> ScriptLine?,
  val typeLineScriptLineOrNullFn: (TypeLine) -> ScriptLine?)

fun <V> Value<V>.script(type: Type, context: ValueScriptContext<V>): Script =
  when (type) {
    is ChoiceType -> script(type.choice, context)
    is StructureType -> script(type.structure, context)
  }

val Value<*>.choiceIndex: Int get() =
  when (this) {
    is BooleanValue -> if (boolean) 0 else 1
    is EmptyValue -> null
    is FunctionValue -> null
    is IndexValue -> index
    is NativeValue -> null
    is RecursiveValue -> null
    is TupleValue -> null
  }!!

val Boolean.choiceIndex: Int get() = if (this) 0 else 1

fun <V> Value<V>.index(typeChoice: TypeChoice): Int =
  if (typeChoice.lineStack.size == 2) (this as BooleanValue).boolean.choiceIndex
  else (this as IndexValue).index

fun <V> Value<V>.script(typeChoice: TypeChoice, context: ValueScriptContext<V>): Script =
  if (typeChoice.isSimple) simpleScript(typeChoice, context)
  else complexScript(typeChoice, context)

fun <V> Value<V>.simpleScript(typeChoice: TypeChoice, context: ValueScriptContext<V>): Script =
  script(value<V>(empty).scriptLine(typeChoice.lineStack.getFromBottom(index(typeChoice))!!, context))

fun <V> Value<V>.complexScript(typeChoice: TypeChoice, context: ValueScriptContext<V>): Script =
  (this as TupleValue).tuple.script(typeChoice, context)

fun <V> ValueTuple<V>.script(typeChoice: TypeChoice, context: ValueScriptContext<V>): Script =
  valueList[0].let { indexValue ->
    valueList[1].let { bodyValue ->
      script(bodyValue.scriptLine(typeChoice.lineStack.getFromBottom(indexValue.index(typeChoice))!!, context))
    }
  }

fun <V> Value<V>.script(typeStructure: TypeStructure, context: ValueScriptContext<V>): Script =
  typeStructure.onlyLineOrNull
    ?.let { script(scriptLine(it, context)) }
    ?: when (this) {
      is BooleanValue -> null
      is EmptyValue -> empty.script(typeStructure)
      is FunctionValue -> null
      is IndexValue -> null
      is NativeValue -> null
      is RecursiveValue -> null
      is TupleValue -> tuple.script(typeStructure, context)
    }!!

fun <V> Boolean.scriptLine(typeChoice: TypeChoice, context: ValueScriptContext<V>): ScriptLine =
  value<V>(empty).scriptLine(typeChoice.lineStack.getFromBottom(if (this) 0 else 1)!!, context)

fun <V> Int.scriptLine(typeChoice: TypeChoice, context: ValueScriptContext<V>): ScriptLine =
  value<V>(empty).scriptLine(typeChoice.lineStack.getFromBottom(this)!!, context)

fun <V> ValueTuple<V>.script(typeStructure: TypeStructure, context: ValueScriptContext<V>): Script =
  zip(stack(*valueList.toTypedArray()), typeStructure.lineStack).map { first!!.scriptLine(second!!, context) }.script

fun <V> Value<V>.scriptLine(typeLine: TypeLine, context: ValueScriptContext<V>): ScriptLine =
  null
    ?: context.customScriptLineOrNullFn(this, typeLine)
    ?: scriptLine(typeLine.atom, context)

fun <V> Value<V>.scriptLine(typeAtom: TypeAtom, context: ValueScriptContext<V>): ScriptLine =
  when (typeAtom) {
    is FunctionTypeAtom -> scriptLine(typeAtom.function, context)
    is PrimitiveTypeAtom -> scriptLine(typeAtom.primitive, context)
  }

@Suppress("unused")
fun <V> Value<V>.scriptLine(typeFunction: TypeFunction, @Suppress("UNUSED_PARAMETER") context: ValueScriptContext<V>): ScriptLine =
  typeFunction.scriptLine(context.typeLineScriptLineOrNullFn)

fun <V> Value<V>.scriptLine(typePrimitive: TypePrimitive, context: ValueScriptContext<V>): ScriptLine =
  when (typePrimitive) {
    is NativeTypePrimitive -> scriptLine(typePrimitive.native_, context)
    is FieldTypePrimitive -> scriptLine(typePrimitive.field, context)
  }

fun <V> Value<V>.scriptLine(@Suppress("UNUSED_PARAMETER") typeNative: TypeNative, context: ValueScriptContext<V>): ScriptLine =
  context.nativeScriptLineFn(native)

fun <V> Value<V>.scriptLine(typeField: TypeField, context: ValueScriptContext<V>): ScriptLine =
  typeField.name lineTo script(typeField.rhsType, context)

fun Empty.script(type: Type): Script =
  script(type.structureOrNull!!)

fun Empty.script(typeStructure: TypeStructure): Script =
  typeStructure.lineStack.mapIt { scriptLine(it) }.script

fun Empty.scriptLine(typeLine: TypeLine): ScriptLine =
  scriptLine(typeLine.atom.primitiveOrNull!!.fieldOrNull!!)

fun Empty.scriptLine(typeField: TypeField): ScriptLine =
  typeField.name lineTo script(typeField.rhsType)
