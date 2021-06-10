package leo.indexed

import leo.Literal
import leo.Stack
import leo.stack

data class Tuple<out T>(val expressionStack: Stack<Expression<T>>)

sealed class Expression<out T>
data class IndexExpression<T>(val index: Int): Expression<T>()
data class AtExpression<T>(val at: At<T>): Expression<T>()
data class FunctionExpression<T>(val function: Function<T>): Expression<T>()
data class InvokeExpression<T>(val invoke: Invoke<T>): Expression<T>()
data class VariableExpression<T>(val variable: Variable): Expression<T>()
data class LiteralExpression<T>(val literal: Literal): Expression<T>()
data class AnyExpression<T>(val any: Any?): Expression<T>()

data class At<out T>(val vector: Expression<T>, val index: Expression<T>)
data class Function<out T>(val paramCount: Int, val body: Expression<T>)
data class Invoke<out T>(val function: Function<T>, val params: Tuple<T>)
data class Variable(val index: Int)

fun <T> tuple(vararg expressions: Expression<T>) = Tuple(stack(*expressions))

fun <T> expression(literal: Literal): Expression<T> = LiteralExpression(literal)
fun <T> expression(index: Int): Expression<T> = IndexExpression(index)
fun <T> expression(at: At<T>): Expression<T> = AtExpression(at)
fun <T> expression(function: Function<T>): Expression<T> = FunctionExpression(function)
fun <T> expression(invoke: Invoke<T>): Expression<T> = InvokeExpression(invoke)
fun <T> expression(variable: Variable): Expression<T> = VariableExpression(variable)
fun <T> anyExpression(any: T): Expression<T> = AnyExpression(any)

fun <T> at(vector: Expression<T>, index: Expression<T>) = At(vector, index)
fun <T> function(paramCount: Int, body: Expression<T>) = Function(paramCount, body)
fun <T> invoke(function: Function<T>, params: Tuple<T>) = Invoke(function, params)
fun variable(index: Int) = Variable(index)
