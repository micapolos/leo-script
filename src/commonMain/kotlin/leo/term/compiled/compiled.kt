package leo.term.compiled

import leo.Empty
import leo.Stack
import leo.Type
import leo.stack

data class Compiled<out T>(val expression: Expression<T>, val type: Type)

sealed class Expression<out T>
data class NativeExpression<T>(val native: T): Expression<T>()
data class EmptyExpression<T>(val empty: Empty): Expression<T>()
data class TupleExpression<T>(val tuple: Tuple<T>): Expression<T>()
data class TupleAtExpression<T>(val tupleAt: TupleAt<T>): Expression<T>()
data class FunctionExpression<T>(val function: Function<T>): Expression<T>()
data class InvokeExpression<T>(val invoke: Invoke<T>): Expression<T>()

data class Tuple<out T>(val compiledStack: Stack<Compiled<T>>)
data class TupleAt<out T>(val tuple: Tuple<T>, val index: Int)

data class Function<out T>(val paramTypeStack: Stack<Type>, val bodyCompiled: Compiled<T>, val isRecursive: Boolean)
data class Invoke<out T>(val functionCompiled: Compiled<T>, val paramTuple: Tuple<T>)

fun <T> Expression<T>.of(type: Type) = Compiled(this, type)

val <T> T.nativeExpression: Expression<T> get() = NativeExpression(this)
fun <T> Empty.expression(): Expression<T> = EmptyExpression(this)
val <T> Tuple<T>.expression: Expression<T> get() = TupleExpression(this)
val <T> TupleAt<T>.expression: Expression<T> get() = TupleAtExpression(this)
val <T> Function<T>.expression: Expression<T> get() = FunctionExpression(this)
val <T> Invoke<T>.expression: Expression<T> get() = InvokeExpression(this)

fun <T> tuple(vararg compileds: Compiled<T>) = Tuple(stack(*compileds))
fun <T> Tuple<T>.at(index: Int) = TupleAt(this, index)
fun <T> Compiled<T>.invoke(tuple: Tuple<T>) = Invoke(this, tuple)
fun <T> function(vararg types: Type, fn: () -> Compiled<T>) = Function(stack(*types), fn(), isRecursive = false)
fun <T> recursive(function: Function<T>) = function.copy(isRecursive = true)
