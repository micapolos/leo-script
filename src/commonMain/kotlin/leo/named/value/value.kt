package leo.named.value

import leo.Literal
import leo.Stack
import leo.named.expression.Expression
import leo.stack

data class Value<out T>(val lineStack: Stack<ValueLine<T>>)

sealed class ValueLine<out T>
data class LiteralValueLine<T>(val literal: Literal): ValueLine<T>()
data class FieldValueLine<T>(val field: Field<T>): ValueLine<T>()
data class FunctionValueLine<T>(val function: Function<T>): ValueLine<T>()
data class AnyValueLine<T>(val any: T): ValueLine<T>()

data class Field<out T>(val name: String, val value: Value<T>)
data class Function<out T>(val expression: Expression<T>)

fun <T> anyValueLine(any: T): ValueLine<T> = AnyValueLine(any)
fun <T> line(field: Field<T>): ValueLine<T> = FieldValueLine(field)
fun <T> valueLine(literal: Literal): ValueLine<T> = LiteralValueLine(literal)
fun <T> line(function: Function<T>): ValueLine<T> = FunctionValueLine(function)

infix fun <T> String.fieldTo(value: Value<T>) = Field(this, value)
infix fun <T> String.lineTo(value: Value<T>) = line(this fieldTo value)
fun <T> function(expression: Expression<T>) = Function(expression)

fun <T> value(vararg lines: ValueLine<T>) = Value(stack(*lines))