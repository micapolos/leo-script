package leo.term.indexed.script

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
import leo.get
import leo.getFromBottom
import leo.line
import leo.lineTo
import leo.literal
import leo.map
import leo.mapIt
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
  when (this) {
    is BooleanValue -> null
    is EmptyValue -> null
    is FunctionValue -> null
    is IndexValue -> null
    is NativeValue -> null
    is RecursiveValue -> null
    is TupleValue -> tuple.script(typeStructure)
  }!!

fun Boolean.scriptLine(typeChoice: TypeChoice): ScriptLine =
  value<Native>(empty).scriptLine(typeChoice.lineStack.get(if (this) 1 else 0)!!)

fun Int.scriptLine(typeChoice: TypeChoice): ScriptLine =
  value<Native>(empty).scriptLine(typeChoice.lineStack.getFromBottom(this)!!)

fun ValueTuple<Native>.script(typeStructure: TypeStructure): Script =
  zip(stack(*valueList.toTypedArray()), typeStructure.lineStack).map { first!!.scriptLine(second!!) }.script

fun Value<Native>.scriptLine(typeLine: TypeLine): ScriptLine =
  scriptLine(typeLine.atom)

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
  type.structureOrNull!!.lineStack.mapIt { scriptLine(it) }.script

fun Empty.scriptLine(typeLine: TypeLine): ScriptLine =
  scriptLine(typeLine.atom.primitiveOrNull!!.fieldOrNull!!)

fun Empty.scriptLine(typeField: TypeField): ScriptLine =
  typeField.name lineTo script(typeField.rhsType)

val Native.scriptLine: ScriptLine get() =
  when (this) {
    is DoubleNative -> line(literal(double))
    is StringNative -> line(literal(string))
    DoubleIsLessThanDoubleNative -> null
    DoubleMinusDoubleNative -> null
    DoublePlusDoubleNative -> null
    DoubleTimesDoubleNative -> null
    ObjectEqualsObjectNative -> null
    StringLengthNative -> null
    StringPlusStringNative -> null
  }!!
