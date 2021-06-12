package leo.named.value

import leo.Literal
import leo.Stack
import leo.named.expression.Expression
import leo.stack

data class Value<out T>(val lineStack: Stack<ValueLine<T>>)

sealed class ValueLine<out T>
data class LiteralValueLine<T>(val literal: Literal): ValueLine<T>()
data class FieldValueLine<T>(val field: ValueField<T>): ValueLine<T>()
data class FunctionValueLine<T>(val function: ValueFunction<T>): ValueLine<T>()
data class AnyValueLine<T>(val any: T): ValueLine<T>()

data class ValueField<out T>(val name: String, val value: Value<T>)
data class ValueFunction<out T>(val expression: Expression<T>)

fun <T> anyValueLine(any: T): ValueLine<T> = AnyValueLine(any)
fun <T> valueLine(literal: Literal): ValueLine<T> = LiteralValueLine(literal)
fun <T> line(field: ValueField<T>): ValueLine<T> = FieldValueLine(field)
fun <T> line(function: ValueFunction<T>): ValueLine<T> = FunctionValueLine(function)

infix fun <T> String.fieldTo(value: Value<T>) = ValueField(this, value)
infix fun <T> String.lineTo(value: Value<T>) = line(this fieldTo value)
fun <T> function(expression: Expression<T>) = ValueFunction(expression)

fun <T> value(vararg lines: ValueLine<T>) = Value(stack(*lines))