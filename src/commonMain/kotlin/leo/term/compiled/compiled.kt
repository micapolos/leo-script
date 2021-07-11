package leo.term.compiled

import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.TypeStructure
import leo.lineTo
import leo.named.value.anyScriptLine
import leo.stack
import leo.term.IndexVariable

data class Compiled<out V>(val expression: Expression<V>, val type: Type) {
  override fun toString() = toScriptLine { it.anyScriptLine }.toString()
}

data class CompiledLine<out V>(val line: Line<V>, val typeLine: TypeLine)
data class CompiledTuple<out V>(val tuple: Tuple<V>, val typeStructure: TypeStructure)

sealed class Expression<out V>
data class TupleExpression<out V>(val tuple: Tuple<V>): Expression<V>()
data class ApplyExpression<V>(val apply: Apply<V>): Expression<V>()
data class VariableExpression<out V>(val variable: IndexVariable): Expression<V>()
data class SelectExpression<V>(val select: Select<V>): Expression<V>()
data class SwitchExpression<V>(val switch: Switch<V>): Expression<V>()

data class Tuple<out V>(val lineStack: Stack<Line<V>>)

sealed class Line<out V>
data class NativeLine<V>(val native: V): Line<V>()
data class FieldLine<V>(val field: Field<V>): Line<V>()
data class FunctionLine<V>(val function: Function<V>): Line<V>()
data class GetLine<V>(val get: Get<V>): Line<V>()

data class Field<out V>(val name: String, val rhs: Compiled<V>)

data class Select<out V>(val choice: TypeChoice, val index: Int, val line: Line<V>)

data class Function<out V>(val paramType: Type, val body: Body<V>)
data class Body<out V>(val compiled: Compiled<V>, val isRecursive: Boolean)

data class Get<out V>(val lhs: Compiled<V>, val index: Int)
data class Apply<out V>(val lhs: Compiled<V>, val rhs: Compiled<V>)
data class Switch<out V>(val lhs: Compiled<V>, val lineStack: Stack<Line<V>>)

fun <V> tuple(vararg lines: Line<V>) = Tuple(stack(*lines))
fun <V> expression(tuple: Tuple<V>): Expression<V> = TupleExpression(tuple)
fun <V> expression(apply: Apply<V>): Expression<V> = ApplyExpression(apply)
fun <V> expression(variable: IndexVariable): Expression<V> = VariableExpression(variable)

fun <V> nativeLine(native: V): Line<V> = NativeLine(native)
fun <V> line(field: Field<V>): Line<V> = FieldLine(field)
fun <V> line(function: Function<V>): Line<V> = FunctionLine(function)
fun <V> line(get: Get<V>): Line<V> = GetLine(get)

fun <V> compiled(expression: Expression<V>, type: Type): Compiled<V> = Compiled(expression, type)
fun <V> compiled(tuple: Tuple<V>, structure: TypeStructure) = CompiledTuple(tuple, structure)
fun <V> compiled(line: Line<V>, typeLine: TypeLine) = CompiledLine(line, typeLine)

fun <V> function(paramType: Type, body: Body<V>) = Function(paramType, body)
fun <V> body(compiled: Compiled<V>) = Body(compiled, isRecursive = false)
fun <V> recursive(body: Body<V>) = body.copy(isRecursive = true)
fun <V> apply(lhs: Compiled<V>, rhs: Compiled<V>) = Apply(lhs, rhs)
fun <V> field(name: String, rhs: Compiled<V>) = Field(name, rhs)
fun <V> get(lhs: Compiled<V>, index: Int) = Get(lhs, index)
fun <V> select(choice: TypeChoice, index: Int, line: Line<V>) = Select(choice, index, line)

infix fun <V> String.lineTo(compiled: Compiled<V>): CompiledLine<V> =
  compiled(line(field(this, compiled)), this lineTo compiled.type)