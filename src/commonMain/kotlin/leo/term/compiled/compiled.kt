package leo.term.compiled

import leo.Empty
import leo.Stack
import leo.Type
import leo.TypeLine
import leo.term.IndexVariable

data class Compiled<out V>(val expression: Expression<V>, val type: Type)

sealed class Expression<out V>
data class EmptyExpression<out V>(val empty: Empty): Expression<V>()
data class AppendExpression<V>(val append: Append<V>): Expression<V>()
data class ContentExpression<V>(val content: Content): Expression<V>()
data class GetExpression<V>(val get: Get<V>): Expression<V>()
data class FunctionExpression<V>(val function: Function<V>): Expression<V>()
data class ApplyExpression<V>(val apply: Apply<V>): Expression<V>()
data class VariableExpression<out V>(val variable: IndexVariable): Expression<V>()
data class SelectExpression<V>(val select: Select<V>): Expression<V>()
data class SwitchExpression<V>(val switch: Switch<V>): Expression<V>()

sealed class Line<out V>
data class NativeLine<V>(val native: V): Line<V>()
data class FieldLine<V>(val field: Field<V>): Line<V>()
data class Field<out V>(val name: String, val rhs: Compiled<V>)

data class Select<out V>(val lineStack: Stack<SelectLine<V>>)

sealed class SelectLine<out V>
data class PickSelectLine<V>(val pick: Pick<V>): SelectLine<V>()
data class DropSelectLine<V>(val drop: Drop): SelectLine<V>()
data class Pick<out V>(val line: Line<V>)
data class Drop(val typeLine: TypeLine)

data class Function<out V>(val type: Type, val body: Body<V>)
data class Body<out V>(val compiled: Compiled<V>, val isRecursive: Boolean)

object Content
data class Append<out V>(val lhs: Compiled<V>, val line: Line<V>)
data class Get<V>(val lhs: Compiled<V>, val index: Int)
data class Apply<out V>(val lhs: Compiled<V>, val rhs: Compiled<V>)
data class Switch<out V>(val lhs: Compiled<V>, val functionStack: Stack<Function<V>>)
