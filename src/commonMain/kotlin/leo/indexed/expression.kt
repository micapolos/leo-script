package leo.indexed

import leo.Literal
import leo.Stack
import leo.stack

data class Tuple<out T>(val expressionStack: Stack<Expression<T>>)

sealed class Expression<out T>
data class TupleExpression<T>(val tuple: Tuple<T>): Expression<T>()
data class AtExpression<T>(val at: At<T>): Expression<T>()
data class IndexedExpression<T>(val indexed: IndexedValue<Expression<T>>): Expression<T>()
data class SwitchExpression<T>(val switch: Switch<T>): Expression<T>()
data class FunctionExpression<T>(val function: Function<T>): Expression<T>()
data class InvokeExpression<T>(val invoke: Invoke<T>): Expression<T>()
data class VariableExpression<T>(val variable: Variable): Expression<T>()
data class LiteralExpression<T>(val literal: Literal): Expression<T>()
data class AnyExpression<T>(val any: Any?): Expression<T>()

data class At<out T>(val vector: Expression<T>, val index: Int)
data class Switch<out T>(val lhs: Expression<T>, val cases: Tuple<T>)
data class Function<out T>(val paramCount: Int, val body: Expression<T>)
data class Invoke<out T>(val function: Expression<T>, val params: Tuple<T>)
data class Variable(val index: Int)

fun <T> tuple(vararg expressions: Expression<T>) = Tuple(stack(*expressions))

fun <T> expression(literal: Literal): Expression<T> = LiteralExpression(literal)
fun <T> expression(tuple: Tuple<T>): Expression<T> = TupleExpression(tuple)
fun <T> expression(at: At<T>): Expression<T> = AtExpression(at)
fun <T> expression(indexed: IndexedValue<Expression<T>>): Expression<T> = IndexedExpression(indexed)
fun <T> expression(switch: Switch<T>): Expression<T> = SwitchExpression(switch)
fun <T> expression(function: Function<T>): Expression<T> = FunctionExpression(function)
fun <T> expression(invoke: Invoke<T>): Expression<T> = InvokeExpression(invoke)
fun <T> expression(variable: Variable): Expression<T> = VariableExpression(variable)
fun <T> anyExpression(any: T): Expression<T> = AnyExpression(any)

fun <T> at(lhs: Expression<T>, index: Int) = At(lhs, index)
fun <T> function(paramCount: Int, body: Expression<T>) = Function(paramCount, body)
fun <T> invoke(function: Expression<T>, params: Tuple<T>) = Invoke(function, params)
fun <T> switch(lhs: Expression<T>, cases: Tuple<T>) = Switch(lhs, cases)
fun variable(index: Int) = Variable(index)

fun <T> expression(): Expression<T> = expression(tuple())