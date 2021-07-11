package leo.term.compiled

import leo.Empty
import leo.Stack
import leo.Type
import leo.term.IndexVariable

data class Compiled<out V>(val expression: Expression<V>, val type: Type)

sealed class Expression<out V>
data class NativeExpression<V>(val native: V): Expression<V>()
data class EmptyExpression<out V>(val empty: Empty): Expression<V>()
data class AppendExpression<V>(val append: Append<V>): Expression<V>()
data class ContentExpression<V>(val content: Content<V>): Expression<V>()
data class GetExpression<V>(val get: Get<V>): Expression<V>()
data class FunctionExpression<V>(val function: Function<V>): Expression<V>()
data class ApplyExpression<V>(val apply: Apply<V>): Expression<V>()
data class VariableExpression<out V>(val variable: IndexVariable): Expression<V>()
data class SelectExpression<V>(val select: Select<V>): Expression<V>()
data class SwitchExpression<V>(val switch: Switch<V>): Expression<V>()

data class Field<out V>(val name: String, val rhs: Compiled<V>)

data class Select<out V>(val index: Int, val field: Field<V>)

data class Function<out V>(val type: Type, val body: Body<V>)
data class Body<out V>(val compiled: Compiled<V>, val isRecursive: Boolean)

data class Content<out V>(val lhs: Compiled<V>)
data class Append<out V>(val lhs: Compiled<V>, val field: Field<V>)
data class Get<V>(val lhs: Compiled<V>, val index: Int)
data class Apply<out V>(val lhs: Compiled<V>, val rhs: Compiled<V>)
data class Switch<out V>(val lhs: Compiled<V>, val fieldStack: Stack<Field<V>>)
