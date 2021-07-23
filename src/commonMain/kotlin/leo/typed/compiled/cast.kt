package leo.typed.compiled

import leo.AtomTypeRecursible
import leo.ChoiceType
import leo.FieldTypePrimitive
import leo.FunctionTypeAtom
import leo.NativeTypePrimitive
import leo.PrimitiveTypeAtom
import leo.RecurseTypeRecursible
import leo.RecursibleTypeLine
import leo.RecursiveTypeLine
import leo.StructureType
import leo.Type
import leo.TypeAtom
import leo.TypeChoice
import leo.TypeField
import leo.TypeLine
import leo.TypePrimitive
import leo.TypeRecurse
import leo.TypeRecursible
import leo.TypeRecursive
import leo.atomOrNull
import leo.base.firstOrNull
import leo.base.ifOrNull
import leo.base.notNullIf
import leo.fieldOrNull
import leo.name
import leo.onlyLineOrNull
import leo.primitiveOrNull
import leo.recurseOrNull
import leo.recursibleOrNull
import leo.seq
import leo.shiftTypeLine

object Identity

sealed class Cast<out V>
data class IdentityCast<V>(val identity: Identity): Cast<V>()
data class ValueCast<V>(val value: V): Cast<V>()

val identity get() = Identity

fun <V> cast(identity: Identity): Cast<V> = IdentityCast(identity)
fun <V> cast(value: V): Cast<V> = ValueCast(value)

fun <V, R> Cast<V>.map(fn: (V) -> R): Cast<R> =
  when (this) {
    is IdentityCast -> cast(identity)
    is ValueCast -> cast(fn(value))
  }

val <V> ValueCast<Expression<V>>.expression: Expression<V> get() = value
val <V> ValueCast<Line<V>>.line: Line<V> get() = value

fun <V> Compiled<V>.castOrNull(toType: Type): Compiled<V>? =
  expression
    .castOrNull(type, toType, null)
    ?.let { cast ->
      when (cast) {
        is IdentityCast -> compiled(expression, toType)
        is ValueCast -> compiled(cast.expression, toType)
      }
    }

fun <V> Expression<V>.castOrNull(fromType: Type, toType: Type, recursiveOrNull: TypeRecursive?): Cast<Expression<V>>? =
  null
    ?: compiled(this, fromType).onlyCompiledLineOrNull?.let { compiledLine ->
      compiledLine.line.castOrNull(compiledLine.typeLine, toType, recursiveOrNull)
    }
    ?: notNullIf(fromType == toType) { cast(identity) }

fun <V> Line<V>.castOrNull(fromTypeLine: TypeLine, toType: Type, recursiveOrNull: TypeRecursive?): Cast<Expression<V>>? =
  when (toType) {
    is StructureType -> toType.structure.onlyLineOrNull
      ?.let { toTypeLine ->
        castOrNull(fromTypeLine, toTypeLine, recursiveOrNull)
          ?.map { expression(tuple(it)) }
      }
    is ChoiceType -> castOrNull(fromTypeLine, toType.choice)
  }

fun <V> Line<V>.castOrNull(fromTypeLine: TypeLine, toTypeLine: TypeLine, recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  when (toTypeLine) {
    is RecursibleTypeLine ->
      fromTypeLine.recursibleOrNull?.let {
        castOrNull(it, toTypeLine.recursible, recursiveOrNull)
      }
    is RecursiveTypeLine ->
      when (fromTypeLine) {
        is RecursibleTypeLine -> castOrNull(fromTypeLine, toTypeLine.recursive.shiftTypeLine, toTypeLine.recursive)
        is RecursiveTypeLine -> notNullIf(fromTypeLine.recursive == toTypeLine.recursive) { cast(identity) }
      }
  }

fun <V> Line<V>.castOrNull(fromTypeRecursible: TypeRecursible, toTypeRecursible: TypeRecursible, recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  when (toTypeRecursible) {
    is AtomTypeRecursible -> fromTypeRecursible.atomOrNull?.let {
      castOrNull(it, toTypeRecursible.atom, recursiveOrNull)
    }
    is RecurseTypeRecursible -> fromTypeRecursible.recurseOrNull?.let {
      castOrNull(it, toTypeRecursible.recurse, recursiveOrNull)
    }
  }

@Suppress("unused")
fun <V> Line<V>.castOrNull(fromTypeRecurse: TypeRecurse, toTypeRecurse: TypeRecurse, @Suppress("UNUSED_PARAMETER") recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  notNullIf(fromTypeRecurse == toTypeRecurse) { cast(identity) }

fun <V> Line<V>.castOrNull(fromTypeAtom: TypeAtom, toTypeAtom: TypeAtom, recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  when (toTypeAtom) {
    is FunctionTypeAtom -> notNullIf(fromTypeAtom == toTypeAtom) { cast(identity) }
    is PrimitiveTypeAtom -> fromTypeAtom.primitiveOrNull?.let {
      castOrNull(it, toTypeAtom.primitive, recursiveOrNull)
    }
  }

fun <V> Line<V>.castOrNull(fromTypePrimitive: TypePrimitive, toTypePrimitive: TypePrimitive, recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  when (toTypePrimitive) {
    is FieldTypePrimitive -> fromTypePrimitive.fieldOrNull?.let {
      castOrNull(it, toTypePrimitive.field, recursiveOrNull)
    }
    is NativeTypePrimitive -> notNullIf(fromTypePrimitive == toTypePrimitive) { cast(identity) }
  }

fun <V> Line<V>.castOrNull(fromTypeField: TypeField, toTypeField: TypeField, recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  ifOrNull(fromTypeField.name == toTypeField.name) {
    fieldOrNull?.let { field ->
      field.rhs.expression.castOrNull(fromTypeField.rhsType, toTypeField.rhsType, recursiveOrNull)?.let { cast ->
        when (cast) {
          is IdentityCast -> cast(line(field(fromTypeField.name, compiled(field.rhs.expression, toTypeField.rhsType))))
          is ValueCast -> cast(line(field(fromTypeField.name, compiled(cast.value, toTypeField.rhsType))))
        }
      }
    }
  }

fun <V> Line<V>.castOrNull(fromTypeLine: TypeLine, toChoice: TypeChoice): Cast<Expression<V>>? =
  toChoice
    .lineStack
    .seq
    .firstOrNull { this == fromTypeLine }
    ?.let { selectedTypeLine ->
      cast(expression(select(toChoice, case(selectedTypeLine.name, this))))
    }
