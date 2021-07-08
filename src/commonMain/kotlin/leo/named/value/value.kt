package leo.named.value

import leo.Stack
import leo.base.fold
import leo.named.evaluator.Dictionary
import leo.named.evaluator.Recursive
import leo.named.evaluator.plus
import leo.named.expression.Doing
import leo.push
import leo.reverse
import leo.seq
import leo.stack

data class Value(val lineStack: Stack<ValueLine>) {
  override fun toString() = scriptLine.toString()
}

sealed class ValueLine
data class FieldValueLine(val field: ValueField) : ValueLine()
data class FunctionValueLine(val function: ValueFunction) : ValueLine()
data class AnyValueLine(val any: Any?) : ValueLine()

data class ValueField(val name: String, val value: Value)
data class ValueFunction(val dictionary: Dictionary, val doing: Doing)

fun anyValueLine(any: Any?): ValueLine = AnyValueLine(any)
fun line(field: ValueField): ValueLine = FieldValueLine(field)
fun line(function: ValueFunction): ValueLine = FunctionValueLine(function)

infix fun String.fieldTo(value: Value) = ValueField(this, value)
infix fun String.lineTo(value: Value) = line(this fieldTo value)
fun function(dictionary: Dictionary, doing: Doing) = ValueFunction(dictionary, doing)

val Value.lineSeq get() = lineStack.reverse.seq

fun Value.make(name: String) = value(name lineTo this)
fun Value.plus(line: ValueLine): Value = lineStack.push(line).let(::Value)
fun Value.with(value: Value): Value = fold(value.lineSeq) { plus(it) }
fun value(vararg lines: ValueLine) = Value(stack(*lines))
fun value(name: String) = value(name lineTo value())

fun ValueFunction.plus(recursive: Recursive) = copy(dictionary = dictionary.plus(recursive.recursiveDictionary))

val ValueLine.anyOrNull: Any? get() = (this as? AnyValueLine)?.any
val ValueLine.fieldOrNull: ValueField? get() = (this as? FieldValueLine)?.field
val ValueLine.functionOrNull: ValueFunction? get() = (this as? FunctionValueLine)?.function
