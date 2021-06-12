package leo.named.evaluator

import leo.Literal
import leo.Stack
import leo.named.Case
import leo.named.Expression

data class Structure<out T>(val valueStack: Stack<Value<T>>)

sealed class Value<out T>
data class LiteralValue<T>(val literal: Literal): Value<T>()
data class FieldValue<T>(val field: Field<T>): Value<T>()
data class ExpressionValue<T>(val expression: Expression<T>): Value<T>()
data class AnyValue<T>(val any: T): Value<T>()

data class Field<out T>(val name: String, val structure: Structure<T>)
data class Function<out T>(val dictionary: Dictionary<T>, val expression: Expression<T>)

fun <T> anyValue(any: T): Value<T> = AnyValue(any)
fun <T> value(field: Field<T>): Value<T> = FieldValue(field)
fun <T> value(literal: Literal): Value<T> = LiteralValue(literal)
fun <T> value(expression: Expression<T>): Value<T> = ExpressionValue(expression)

infix fun <T> String.fieldTo(structure: Structure<T>) = Field(this, structure)
infix fun <T> String.valueTo(structure: Structure<T>) = value(this fieldTo structure)

fun <T> Value<T>.get(name: String): Value<T> = TODO()
fun <T> Value<T>.invoke(params: Structure<T>): Value<T> = TODO()
fun <T> Value<T>.switch(cases: Stack<Case<T>>): Value<T> = TODO()
