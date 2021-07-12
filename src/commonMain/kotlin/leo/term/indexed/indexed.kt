package leo.term.indexed

import leo.Empty
import leo.IndexVariable

sealed class Expression<out V>

data class NativeExpression<out V>(val native: V): Expression<V>()
data class EmptyExpression<out V>(val empty: Empty): Expression<V>()
data class TupleExpression<out V>(val tuple: Tuple<V>): Expression<V>()
data class TupleGetExpression<out V>(val get: TupleGet<V>): Expression<V>()
data class IndexExpression<out V>(val index: Int): Expression<V>()
data class IndexSwitchExpression<out V>(val switch: IndexSwitch<V>): Expression<V>()
data class IndexedExpression<out V>(val indexed: Indexed<V>): Expression<V>()
data class IndexedSwitchExpression<out V>(val switch: IndexedSwitch<V>): Expression<V>()
data class FunctionExpression<out V>(val function: Function<V>): Expression<V>()
data class RecursiveExpression<out V>(val recursive: Recursive<V>): Expression<V>()
data class InvokeExpression<out V>(val invoke: Invoke<V>): Expression<V>()
data class VariableExpression<out V>(val variable: IndexVariable): Expression<V>()

data class Tuple<out V>(val list: List<Expression<V>>)
data class TupleGet<out V>(val lhs: Expression<V>, val index: Int)
data class Indexed<out V>(val index: Int, val expression: Expression<V>)
data class IndexSwitch<out V>(val lhs: Expression<V>, val cases: List<Expression<V>>)
data class IndexedSwitch<out V>(val lhs: Expression<V>, val cases: List<Expression<V>>)
data class Function<out V>(val arity: Int, val expression: Expression<V>)
data class Recursive<out V>(val function: Function<V>)
data class Invoke<out V>(val lhs: Expression<V>, val params: List<Expression<V>>)

fun <V> nativeExpression(native: V): Expression<V> = NativeExpression(native)
fun <V> expression(empty: Empty): Expression<V> = EmptyExpression(empty)
fun <V> expression(tuple: Tuple<V>): Expression<V> = TupleExpression(tuple)
fun <V> expression(get: TupleGet<V>): Expression<V> = TupleGetExpression(get)
fun <V> expression(index: Int): Expression<V> = IndexExpression(index)
fun <V> expression(switch: IndexSwitch<V>): Expression<V> = IndexSwitchExpression(switch)
fun <V> expression(indexed: Indexed<V>): Expression<V> = IndexedExpression(indexed)
fun <V> expression(switch: IndexedSwitch<V>): Expression<V> = IndexedSwitchExpression(switch)
fun <V> expression(function: Function<V>): Expression<V> = FunctionExpression(function)
fun <V> expression(recursive: Recursive<V>): Expression<V> = RecursiveExpression(recursive)
fun <V> expression(invoke: Invoke<V>): Expression<V> = InvokeExpression(invoke)
fun <V> expression(variable: IndexVariable): Expression<V> = VariableExpression(variable)

fun <V> tuple(vararg expressions: Expression<V>) = Tuple(listOf(*expressions))
fun <V> get(lhs: Expression<V>, index: Int) = TupleGet(lhs, index)
fun <V> indexed(index: Int, expression: Expression<V>) = Indexed(index, expression)
fun <V> indexSwitch(lhs: Expression<V>, vararg cases: Expression<V>) = IndexSwitch(lhs, listOf(*cases))
fun <V> indexedSwitch(lhs: Expression<V>, vararg cases: Expression<V>) = IndexedSwitch(lhs, listOf(*cases))
fun <V> function(arity: Int, expression: Expression<V>) = Function(arity, expression)
fun <V> recursive(function: Function<V>) = Recursive(function)
fun <V> invoke(lhs: Expression<V>, vararg params: Expression<V>) = Invoke(lhs, listOf(*params))