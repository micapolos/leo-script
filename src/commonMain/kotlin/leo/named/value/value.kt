package leo.named.value

import leo.Literal
import leo.Stack
import leo.named.expression.Body
import leo.push
import leo.stack

data class Value(val lineStack: Stack<ValueLine>)

sealed class ValueLine
data class LiteralValueLine(val literal: Literal): ValueLine()
data class FieldValueLine(val field: ValueField): ValueLine()
data class FunctionValueLine(val function: ValueFunction): ValueLine()
data class AnyValueLine(val any: Any?): ValueLine()

data class ValueField(val name: String, val value: Value)
data class ValueFunction(val body: Body)

fun anyValueLine(any: Any?): ValueLine = AnyValueLine(any)
fun valueLine(literal: Literal): ValueLine = LiteralValueLine(literal)
fun line(field: ValueField): ValueLine = FieldValueLine(field)
fun line(function: ValueFunction): ValueLine = FunctionValueLine(function)

infix fun String.fieldTo(value: Value) = ValueField(this, value)
infix fun String.lineTo(value: Value) = line(this fieldTo value)
fun function(body: Body) = ValueFunction(body)

fun Value.plus(line: ValueLine): Value = lineStack.push(line).let(::Value)
fun value(vararg lines: ValueLine) = Value(stack(*lines))