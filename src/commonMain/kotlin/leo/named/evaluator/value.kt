package leo.named.evaluator

import leo.Literal
import leo.Stack
import leo.named.Expression

data class Structure<out T>(val valueStack: Stack<Value<T>>)

sealed class Value<out T>
data class LiteralValue<T>(val literal: Literal): Value<T>()
data class FieldValue<T>(val field: Field<T>): Value<T>()
data class FunctionValue<T>(val function: Function<T>): Value<T>()
data class AnyValue<T>(val any: T): Value<T>()

data class Field<out T>(val name: String, val structure: Structure<T>)
data class Function<out T>(val dictionary: Dictionary<T>, val expression: Expression<T>)

fun <T> anyValue(any: T): Value<T> = AnyValue(any)
fun <T> value(field: Field<T>): Value<T> = FieldValue(field)

infix fun <T> String.fieldTo(structure: Structure<T>) = Field(this, structure)
infix fun <T> String.valueTo(structure: Structure<T>) = value(this fieldTo structure)