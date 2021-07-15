package leo.term.indexed.native

import leo.AnyTypePrimitive
import leo.ChoiceType
import leo.Empty
import leo.FieldTypePrimitive
import leo.FunctionTypeAtom
import leo.PrimitiveTypeAtom
import leo.Script
import leo.ScriptLine
import leo.StructureType
import leo.Type
import leo.TypeAny
import leo.TypeAtom
import leo.TypeChoice
import leo.TypeField
import leo.TypeFunction
import leo.TypeLine
import leo.TypePrimitive
import leo.TypeStructure
import leo.atom
import leo.empty
import leo.fieldOrNull
import leo.getFromBottom
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
import leo.stack
import leo.structureOrNull
import leo.term.compiler.native.DoubleIsLessThanDoubleNative
import leo.term.compiler.native.DoubleMinusDoubleNative
import leo.term.compiler.native.DoubleNative
import leo.term.compiler.native.DoublePlusDoubleNative
import leo.term.compiler.native.DoubleTimesDoubleNative
import leo.term.compiler.native.Native
import leo.term.compiler.native.ObjectEqualsObjectNative
import leo.term.compiler.native.StringLengthNative
import leo.term.compiler.native.StringNative
import leo.term.compiler.native.StringPlusStringNative
import leo.term.compiler.native.nativeNumberTypeLine
import leo.term.compiler.native.nativeTextTypeLine
import leo.term.indexed.BooleanValue
import leo.term.indexed.EmptyValue
import leo.term.indexed.FunctionValue
import leo.term.indexed.IndexValue
import leo.term.indexed.NativeValue
import leo.term.indexed.RecursiveValue
import leo.term.indexed.TupleValue
import leo.term.indexed.Value
import leo.term.indexed.ValueTuple
import leo.term.indexed.native
import leo.term.indexed.value
import leo.zip

fun Value<Native>.script(type: Type): Script =
  when (type) {
    is ChoiceType -> script(type.choice)
    is StructureType -> script(type.structure)
  }

fun Value<Native>.script(typeChoice: TypeChoice): Script =
  when (this) {
    is BooleanValue -> script(boolean.scriptLine(typeChoice))
    is EmptyValue -> null
    is FunctionValue -> null
    is IndexValue -> script(index.scriptLine(typeChoice))
    is NativeValue -> null
    is RecursiveValue -> null
    is TupleValue -> null
  }!!

fun Value<Native>.script(typeStructure: TypeStructure): Script =
  typeStructure.onlyLineOrNull
    ?.let { script(scriptLine(it)) }
    ?: when (this) {
      is BooleanValue -> null
      is EmptyValue -> empty.script(typeStructure)
      is FunctionValue -> null
      is IndexValue -> null
      is NativeValue -> null
      is RecursiveValue -> null
      is TupleValue -> tuple.script(typeStructure)
    }!!

fun Boolean.scriptLine(typeChoice: TypeChoice): ScriptLine =
  value<Native>(empty).scriptLine(typeChoice.lineStack.getFromBottom(if (this) 0 else 1)!!)

fun Int.scriptLine(typeChoice: TypeChoice): ScriptLine =
  value<Native>(empty).scriptLine(typeChoice.lineStack.getFromBottom(this)!!)

fun ValueTuple<Native>.script(typeStructure: TypeStructure): Script =
  zip(stack(*valueList.toTypedArray()), typeStructure.lineStack).map { first!!.scriptLine(second!!) }.script

fun Value<Native>.scriptLine(typeLine: TypeLine): ScriptLine =
  when (typeLine) {
    nativeTextTypeLine -> native.scriptLine
    nativeNumberTypeLine -> native.scriptLine
    else -> scriptLine(typeLine.atom)
  }

fun Value<Native>.scriptLine(typeAtom: TypeAtom): ScriptLine =
  when (typeAtom) {
    is FunctionTypeAtom -> scriptLine(typeAtom.function)
    is PrimitiveTypeAtom -> scriptLine(typeAtom.primitive)
  }

@Suppress("unused")
fun Value<Native>.scriptLine(typeFunction: TypeFunction): ScriptLine =
  typeFunction.scriptLine

fun Value<Native>.scriptLine(typePrimitive: TypePrimitive): ScriptLine =
  when (typePrimitive) {
    is AnyTypePrimitive -> scriptLine(typePrimitive.any)
    is FieldTypePrimitive -> scriptLine(typePrimitive.field)
  }

fun Value<Native>.scriptLine(@Suppress("UNUSED_PARAMETER") typeAny: TypeAny): ScriptLine =
  native.scriptLine

fun Value<Native>.scriptLine(typeField: TypeField): ScriptLine =
  typeField.name lineTo script(typeField.rhsType)

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
