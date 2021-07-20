package leo.typed.indexed.native

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
import leo.line
import leo.lineTo
import leo.literal
import leo.map
import leo.mapIt
import leo.nativeName
import leo.onlyLineOrNull
import leo.primitiveOrNull
import leo.script
import leo.scriptLine
import leo.size
import leo.stack
import leo.structureOrNull
import leo.typed.compiler.native.DoubleIsLessThanDoubleNative
import leo.typed.compiler.native.DoubleMinusDoubleNative
import leo.typed.compiler.native.DoubleNative
import leo.typed.compiler.native.DoublePlusDoubleNative
import leo.typed.compiler.native.DoubleTimesDoubleNative
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.ObjectEqualsObjectNative
import leo.typed.compiler.native.StringLengthNative
import leo.typed.compiler.native.StringNative
import leo.typed.compiler.native.StringPlusStringNative
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextTypeLine
import leo.typed.indexed.BooleanValue
import leo.typed.indexed.EmptyValue
import leo.typed.indexed.FunctionValue
import leo.typed.indexed.IndexValue
import leo.typed.indexed.NativeValue
import leo.typed.indexed.RecursiveValue
import leo.typed.indexed.TupleValue
import leo.typed.indexed.Value
import leo.typed.indexed.ValueTuple
import leo.typed.indexed.native
import leo.typed.indexed.value
import leo.zip

fun Value<Native>.script(type: Type): Script =
  script(type) { it.scriptLine }

fun <V> Value<V>.script(type: Type, fn: (V) -> ScriptLine): Script =
  when (type) {
    is ChoiceType -> script(type.choice, fn)
    is StructureType -> script(type.structure, fn)
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

fun <V> Value<V>.script(typeChoice: TypeChoice, fn: (V) -> ScriptLine): Script =
  if (typeChoice.isSimple) simpleScript(typeChoice, fn)
  else complexScript(typeChoice, fn)

fun <V> Value<V>.simpleScript(typeChoice: TypeChoice, fn: (V) -> ScriptLine): Script =
  script(value<V>(empty).scriptLine(typeChoice.lineStack.getFromBottom(index(typeChoice))!!, fn))

fun <V> Value<V>.complexScript(typeChoice: TypeChoice, fn: (V) -> ScriptLine): Script =
  (this as TupleValue).tuple.script(typeChoice, fn)

fun <V> ValueTuple<V>.script(typeChoice: TypeChoice, fn: (V) -> ScriptLine): Script =
  valueList[0].let { indexValue ->
    valueList[1].let { bodyValue ->
      script(bodyValue.scriptLine(typeChoice.lineStack.getFromBottom(indexValue.index(typeChoice))!!, fn))
    }
  }

fun <V> Value<V>.script(typeStructure: TypeStructure, fn: (V) -> ScriptLine): Script =
  typeStructure.onlyLineOrNull
    ?.let { script(scriptLine(it, fn)) }
    ?: when (this) {
      is BooleanValue -> null
      is EmptyValue -> empty.script(typeStructure)
      is FunctionValue -> null
      is IndexValue -> null
      is NativeValue -> null
      is RecursiveValue -> null
      is TupleValue -> tuple.script(typeStructure, fn)
    }!!

fun <V> Boolean.scriptLine(typeChoice: TypeChoice, fn: (V) -> ScriptLine): ScriptLine =
  value<V>(empty).scriptLine(typeChoice.lineStack.getFromBottom(if (this) 0 else 1)!!, fn)

fun <V> Int.scriptLine(typeChoice: TypeChoice, fn: (V) -> ScriptLine): ScriptLine =
  value<V>(empty).scriptLine(typeChoice.lineStack.getFromBottom(this)!!, fn)

fun <V> ValueTuple<V>.script(typeStructure: TypeStructure, fn: (V) -> ScriptLine): Script =
  zip(stack(*valueList.toTypedArray()), typeStructure.lineStack).map { first!!.scriptLine(second!!, fn) }.script

fun <V> Value<V>.scriptLine(typeLine: TypeLine, fn: (V) -> ScriptLine): ScriptLine =
  when (typeLine) {
    nativeTextTypeLine -> fn(native) // TODO: This should be configurable
    nativeNumberTypeLine -> fn(native) // TODO: This should be configurable
    else -> scriptLine(typeLine.atom, fn)
  }

fun <V> Value<V>.scriptLine(typeAtom: TypeAtom, fn: (V) -> ScriptLine): ScriptLine =
  when (typeAtom) {
    is FunctionTypeAtom -> scriptLine(typeAtom.function, fn)
    is PrimitiveTypeAtom -> scriptLine(typeAtom.primitive, fn)
  }

@Suppress("unused")
fun <V> Value<V>.scriptLine(typeFunction: TypeFunction, @Suppress("UNUSED_PARAMETER") fn: (V) -> ScriptLine): ScriptLine =
  typeFunction.scriptLine

fun <V> Value<V>.scriptLine(typePrimitive: TypePrimitive, fn: (V) -> ScriptLine): ScriptLine =
  when (typePrimitive) {
    is NativeTypePrimitive -> scriptLine(typePrimitive.native_, fn)
    is FieldTypePrimitive -> scriptLine(typePrimitive.field, fn)
  }

fun <V> Value<V>.scriptLine(@Suppress("UNUSED_PARAMETER") typeNative: TypeNative, fn: (V) -> ScriptLine): ScriptLine =
  fn(native)

fun <V> Value<V>.scriptLine(typeField: TypeField, fn: (V) -> ScriptLine): ScriptLine =
  typeField.name lineTo script(typeField.rhsType, fn)

fun Empty.script(type: Type): Script =
  script(type.structureOrNull!!)

fun Empty.script(typeStructure: TypeStructure): Script =
  typeStructure.lineStack.mapIt { scriptLine(it) }.script

fun Empty.scriptLine(typeLine: TypeLine): ScriptLine =
  scriptLine(typeLine.atom.primitiveOrNull!!.fieldOrNull!!)

fun Empty.scriptLine(typeField: TypeField): ScriptLine =
  typeField.name lineTo script(typeField.rhsType)

val Native.scriptLine: ScriptLine get() =
  when (this) {
    is DoubleNative ->
      line(literal(double))
    is StringNative ->
      line(literal(string))
    DoubleIsLessThanDoubleNative ->
      nativeName lineTo script(
        "double" lineTo script(),
        "is" lineTo script("less" lineTo script("than" lineTo script("double"))))
    DoubleMinusDoubleNative ->
      nativeName lineTo script(
        "double" lineTo script(),
        "minus" lineTo script("double"))
    DoublePlusDoubleNative ->
      nativeName lineTo script(
        "double" lineTo script(),
        "plus" lineTo script("double"))
    DoubleTimesDoubleNative ->
      nativeName lineTo script(
        "double" lineTo script(),
        "times" lineTo script("double"))
    ObjectEqualsObjectNative ->
      nativeName lineTo script(
        "object" lineTo script(),
        "equals" lineTo script("object"))
    StringLengthNative ->
      nativeName lineTo script(
        "length" lineTo script("string"))
    StringPlusStringNative ->
      nativeName lineTo script(
        "string" lineTo script(),
        "plus" lineTo script("string"))
  }
