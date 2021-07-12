package leo.term.indexed

import leo.Empty
import leo.IndexVariable

sealed class Expression<out V>
data class NativeExpression<out V>(val native: V): Expression<V>()
data class EmptyExpression<out V>(val empty: Empty): Expression<V>()
data class TupleExpression<out V>(val tuple: Tuple<V>): Expression<V>()
data class GetExpression<out V>(val get: Get<V>): Expression<V>()
data class FunctionExpression<out V>(val function: Function<V>): Expression<V>()
data class RecursiveExpression<out V>(val recursive: Recursive<V>): Expression<V>()
data class InvokeExpression<out V>(val invoke: Invoke<V>): Expression<V>()
data class VariableExpression<out V>(val variable: IndexVariable): Expression<V>()
data class IndexExpression<out V>(val index: Int): Expression<V>()
data class IndexSwitchExpression<out V>(val switch: IndexSwitch<V>): Expression<V>()
data class IndexedExpression<out V>(val indexed: Indexed<V>): Expression<V>()
data class IndexedSwitchExpression<out V>(val switch: IndexedSwitch<V>): Expression<V>()

data class Tuple<out V>(val list: List<Expression<V>>)
data class Indexed<out V>(val index: Int, val expression: Expression<V>)
data class Function<out V>(val arity: Int, val expression: Expression<V>, val isRecursive: Boolean)
data class Recursive<out V>(val function: Function<V>)
data class IndexSwitch<out V>(val lhs: Expression<V>, val cases: List<Expression<V>>)
data class IndexedSwitch<out V>(val lhs: Expression<V>, val cases: List<Expression<V>>)
data class Invoke<out V>(val lhs: Expression<V>, val params: List<Expression<V>>)
data class Get<out V>(val lhs: Expression<V>, val index: Int)
