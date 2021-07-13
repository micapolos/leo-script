package leo.term.indexed

import leo.Empty
import leo.IndexVariable

sealed class Expression<out V>

data class NativeExpression<out V>(val native: V): Expression<V>()
data class EmptyExpression<out V>(val empty: Empty): Expression<V>()
data class TupleExpression<out V>(val tuple: ExpressionTuple<V>): Expression<V>()
data class GetExpression<out V>(val get: ExpressionGet<V>): Expression<V>()
data class IndexExpression<out V>(val index: ExpressionIndex): Expression<V>()
data class IndexSwitchExpression<out V>(val switch: ExpressionSwitch<V>): Expression<V>()
data class IndexedExpression<out V>(val indexed: ExpressionIndexed<V>): Expression<V>()
data class IndexedSwitchExpression<out V>(val switch: ExpressionIndexedSwitch<V>): Expression<V>()
data class FunctionExpression<out V>(val function: ExpressionFunction<V>): Expression<V>()
data class RecursiveExpression<out V>(val recursive: ExpressionRecursive<V>): Expression<V>()
data class InvokeExpression<out V>(val invoke: ExpressionInvoke<V>): Expression<V>()
data class VariableExpression<out V>(val variable: IndexVariable): Expression<V>()

data class ExpressionTuple<out V>(val expressionList: List<Expression<V>>)
data class ExpressionGet<out V>(val lhs: Expression<V>, val index: Int)
data class ExpressionIndex(val index: Int, val size: Int)
data class ExpressionIndexed<out V>(val index: Int, val size: Int, val expression: Expression<V>)
data class ExpressionSwitch<out V>(val lhs: Expression<V>, val cases: List<Expression<V>>)
data class ExpressionIndexedSwitch<out V>(val lhs: Expression<V>, val cases: List<Expression<V>>)
data class ExpressionFunction<out V>(val arity: Int, val expression: Expression<V>)
data class ExpressionRecursive<out V>(val function: ExpressionFunction<V>)
data class ExpressionInvoke<out V>(val lhs: Expression<V>, val params: List<Expression<V>>)

fun <V> nativeExpression(native: V): Expression<V> = NativeExpression(native)
fun <V> expression(empty: Empty): Expression<V> = EmptyExpression(empty)
fun <V> expression(tuple: ExpressionTuple<V>): Expression<V> = TupleExpression(tuple)
fun <V> expression(get: ExpressionGet<V>): Expression<V> = GetExpression(get)
fun <V> expression(index: ExpressionIndex): Expression<V> = IndexExpression(index)
fun <V> expression(switch: ExpressionSwitch<V>): Expression<V> = IndexSwitchExpression(switch)
fun <V> expression(indexed: ExpressionIndexed<V>): Expression<V> = IndexedExpression(indexed)
fun <V> expression(switch: ExpressionIndexedSwitch<V>): Expression<V> = IndexedSwitchExpression(switch)
fun <V> expression(function: ExpressionFunction<V>): Expression<V> = FunctionExpression(function)
fun <V> expression(recursive: ExpressionRecursive<V>): Expression<V> = RecursiveExpression(recursive)
fun <V> expression(invoke: ExpressionInvoke<V>): Expression<V> = InvokeExpression(invoke)
fun <V> expression(variable: IndexVariable): Expression<V> = VariableExpression(variable)

fun <V> tuple(vararg expressions: Expression<V>) = ExpressionTuple(listOf(*expressions))
fun <V> get(lhs: Expression<V>, index: Int) = ExpressionGet(lhs, index)
fun index(index: Int, size: Int) = ExpressionIndex(index, size)
fun <V> indexed(index: Int, size: Int, expression: Expression<V>) = ExpressionIndexed(index, size, expression)
fun <V> switch(lhs: Expression<V>, vararg cases: Expression<V>) = ExpressionSwitch(lhs, listOf(*cases))
fun <V> indexedSwitch(lhs: Expression<V>, vararg cases: Expression<V>) = ExpressionIndexedSwitch(lhs, listOf(*cases))
fun <V> function(arity: Int, expression: Expression<V>) = ExpressionFunction(arity, expression)
fun <V> recursive(function: ExpressionFunction<V>) = ExpressionRecursive(function)
fun <V> invoke(lhs: Expression<V>, vararg params: Expression<V>) = ExpressionInvoke(lhs, listOf(*params))

fun <V> expression(vararg expressions: Expression<V>) = expression(tuple(*expressions))
fun <V> Expression<V>.invoke(vararg params: Expression<V>) = expression(leo.term.indexed.invoke(this, *params))
fun <V> Expression<V>.get(index: Int) = expression(get(this, index))
fun <V> Expression<V>.switch(vararg cases: Expression<V>) = expression(leo.term.indexed.switch(this, *cases))
fun <V> Expression<V>.indexedSwitch(vararg cases: Expression<V>): Expression<V> = expression(leo.term.indexed.indexedSwitch(this, *cases))
