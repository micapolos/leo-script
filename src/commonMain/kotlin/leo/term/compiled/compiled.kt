package leo.term.compiled

import leo.Stack
import leo.Type
import leo.TypeLine
import leo.stack
import leo.term.IndexVariable
import leo.type

data class Compiled<out V>(val expression: Expression<V>, val type: Type)
data class CompiledLine<out V>(val line: Line<V>, val typeLine: TypeLine)

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

data class Select<out V>(val index: Int, val line: Line<V>)

data class Function<out V>(val paramType: Type, val body: Body<V>)
data class Body<out V>(val compiled: Compiled<V>, val isRecursive: Boolean)

data class Get<V>(val lhs: Compiled<V>, val index: Int)
data class Apply<out V>(val lhs: Compiled<V>, val rhs: Compiled<V>)
data class Switch<out V>(val lhs: Compiled<V>, val lineStack: Stack<Line<V>>)

fun <V> tuple(vararg lines: Line<V>) = Tuple(stack(*lines))
fun <V> expression(tuple: Tuple<V>): Expression<V> = TupleExpression(tuple)
fun <V> expression(apply: Apply<V>): Expression<V> = ApplyExpression(apply)

fun <V> nativeLine(native: V): Line<V> = NativeLine(native)
fun <V> line(field: Field<V>): Line<V> = FieldLine(field)
fun <V> line(function: Function<V>): Line<V> = FunctionLine(function)

fun <V> compiled(): Compiled<V> = Compiled(expression(tuple()), type())
fun <V> compiled(expression: Expression<V>, type: Type): Compiled<V> = Compiled(expression, type)
fun <V> function(paramType: Type, body: Body<V>) = Function(paramType, body)
fun <V> body(compiled: Compiled<V>) = Body(compiled, isRecursive = false)
fun <V> recursive(body: Body<V>) = body.copy(isRecursive = true)
fun <V> apply(lhs: Compiled<V>, rhs: Compiled<V>) = Apply(lhs, rhs)
fun <V> field(name: String, rhs: Compiled<V>) = Field(name, rhs)
fun <V> get(lhs: Compiled<V>, index: Int) = Get(lhs, index)