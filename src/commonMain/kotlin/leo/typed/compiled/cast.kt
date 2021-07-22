package leo.typed.compiled

import leo.AtomTypeRecursible
import leo.ChoiceType
import leo.Empty
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
import leo.TypeRecursible
import leo.TypeRecursive
import leo.atomOrNull
import leo.base.firstOrNull
import leo.base.ifOrNull
import leo.base.notNullIf
import leo.empty
import leo.fieldOrNull
import leo.line
import leo.nameOrNull
import leo.onlyLineOrNull
import leo.primitiveOrNull
import leo.recursibleOrNull
import leo.seq

sealed class Cast<out V>
data class EmptyCast<V>(val empty: Empty): Cast<V>()
data class ValueCast<V>(val value: V): Cast<V>()

fun <V> cast(empty: Empty): Cast<V> = EmptyCast(empty)
fun <V> cast(value: V): Cast<V> = ValueCast(value)

val <V> ValueCast<Expression<V>>.expression: Expression<V> get() = value
val <V> ValueCast<Line<V>>.line: Line<V> get() = value

fun <V> Compiled<V>.castOrNull(toType: Type): Compiled<V>? =
  expression
    .castOrNull(type, toType, null)
    ?.let { cast ->
      when (cast) {
        is EmptyCast -> compiled(expression, toType)
        is ValueCast -> compiled(cast.expression, toType)
      }
    }

fun <V> Expression<V>.castOrNull(fromType: Type, toType: Type, recursiveOrNull: TypeRecursive?): Cast<Expression<V>>? =
  null
    ?: compiled(this, fromType).onlyCompiledLineOrNull?.let { compiledLine ->
      compiledLine.line.castOrNull(compiledLine.typeLine, toType, recursiveOrNull)
    }
    ?: notNullIf(fromType == toType) { cast(empty) }

fun <V> Line<V>.castOrNull(fromTypeLine: TypeLine, toType: Type, recursiveOrNull: TypeRecursive?): Cast<Expression<V>>? =
  when (toType) {
    is StructureType -> toType.structure.onlyLineOrNull
      ?.let { toTypeLine ->
        castOrNull(fromTypeLine, toTypeLine, recursiveOrNull)
          ?.let { cast ->
            when (cast) {
              is EmptyCast -> cast(empty)
              is ValueCast -> cast(expression(tuple(cast.line)))
            }
          }
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
      castOrNull(fromTypeLine, toTypeLine.recursive.line, toTypeLine.recursive)?.let { cast ->
        when (cast) {
          is EmptyCast -> cast(empty)
          is ValueCast -> cast
        }
      }
  }

fun <V> Line<V>.castOrNull(fromTypeRecursible: TypeRecursible, toTypeRecursible: TypeRecursible, recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  when (toTypeRecursible) {
    is AtomTypeRecursible -> fromTypeRecursible.atomOrNull?.let {
      castOrNull(it, toTypeRecursible.atom, recursiveOrNull)
    }
    is RecurseTypeRecursible ->
      recursiveOrNull?.let { recursive ->
        notNullIf(line(fromTypeRecursible) == recursive.line) {
          cast(empty)
        }
      }
  }

fun <V> Line<V>.castOrNull(fromTypeAtom: TypeAtom, toTypeAtom: TypeAtom, recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  when (toTypeAtom) {
    is FunctionTypeAtom -> notNullIf(fromTypeAtom == toTypeAtom) { cast(empty) }
    is PrimitiveTypeAtom -> fromTypeAtom.primitiveOrNull?.let {
      castOrNull(it, toTypeAtom.primitive, recursiveOrNull)
    }
  }

fun <V> Line<V>.castOrNull(fromTypePrimitive: TypePrimitive, toTypePrimitive: TypePrimitive, recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  when (toTypePrimitive) {
    is FieldTypePrimitive -> fromTypePrimitive.fieldOrNull?.let {
      castOrNull(it, toTypePrimitive.field, recursiveOrNull)
    }
    is NativeTypePrimitive -> notNullIf(fromTypePrimitive == toTypePrimitive) { cast(empty) }
  }

fun <V> Line<V>.castOrNull(fromTypeField: TypeField, toTypeField: TypeField, recursiveOrNull: TypeRecursive?): Cast<Line<V>>? =
  ifOrNull(fromTypeField.name == toTypeField.name) {
    fieldOrNull?.let { field ->
      field.rhs.expression.castOrNull(fromTypeField.rhsType, toTypeField.rhsType, recursiveOrNull)?.let { cast ->
        when (cast) {
          is EmptyCast -> cast(line(field(fromTypeField.name, compiled(field.rhs.expression, toTypeField.rhsType))))
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
      selectedTypeLine.nameOrNull?.let { name ->
        cast(expression(select(toChoice, case(name, this))))
      }
    }
