package leo.term.compiled

import leo.Stack
import leo.Type
import leo.term.IndexVariable

data class Compiled<out V>(val expression: Expression<V>, val type: Type)

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
data class GetLine<V>(val get: Get<V>): Line<V>()
data class FunctionLine<V>(val function: Function<V>): Line<V>()

data class Field<out V>(val name: String, val rhs: Compiled<V>)

data class Select<out V>(val index: Int, val line: Line<V>)

data class Function<out V>(val type: Type, val body: Body<V>)
data class Body<out V>(val compiled: Compiled<V>, val isRecursive: Boolean)

data class Get<V>(val lhs: Compiled<V>, val index: Int)
data class Apply<out V>(val lhs: Compiled<V>, val rhs: Compiled<V>)
data class Switch<out V>(val lhs: Compiled<V>, val lineStack: Stack<Line<V>>)
