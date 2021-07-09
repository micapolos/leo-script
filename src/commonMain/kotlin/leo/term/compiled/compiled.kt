package leo.term.compiled

import leo.Empty
import leo.Stack
import leo.TypeLine
import leo.stack
import leo.term.IndexVariable

data class Compiled<out T>(val expression: Expression<T>, val typeLine: TypeLine)

sealed class Expression<out T>
data class NativeExpression<T>(val native: T): Expression<T>()
data class EmptyExpression<T>(val empty: Empty): Expression<T>()
data class TupleAtExpression<T>(val tupleAt: TupleAt<T>): Expression<T>()
data class FunctionExpression<T>(val function: Function<T>): Expression<T>()
data class InvokeExpression<T>(val invoke: Invoke<T>): Expression<T>()
data class VariableExpression<T>(val variable: IndexVariable): Expression<T>()

data class Tuple<out T>(val compiledStack: Stack<Compiled<T>>)
data class TupleAt<out T>(val tuple: Tuple<T>, val index: Int)

data class Function<out T>(val paramTypeLineStack: Stack<TypeLine>, val bodyTuple: Tuple<T>, val isRecursive: Boolean)
data class Invoke<out T>(val functionCompiled: Compiled<T>, val paramTuple: Tuple<T>)

fun <T> Expression<T>.of(typeLine: TypeLine) = Compiled(this, typeLine)

val <T> T.nativeExpression: Expression<T> get() = NativeExpression(this)
fun <T> Empty.expression(): Expression<T> = EmptyExpression(this)
val <T> TupleAt<T>.expression: Expression<T> get() = TupleAtExpression(this)
val <T> Function<T>.expression: Expression<T> get() = FunctionExpression(this)
val <T> Invoke<T>.expression: Expression<T> get() = InvokeExpression(this)
fun <T> IndexVariable.expression(): Expression<T> = VariableExpression(this)

fun <T> tuple(vararg compileds: Compiled<T>) = Tuple(stack(*compileds))
fun <T> Tuple<T>.at(index: Int) = TupleAt(this, index)
fun <T> Compiled<T>.invoke(tuple: Tuple<T>) = Invoke(this, tuple)
fun <T> function(vararg typeLines: TypeLine, fn: () -> Tuple<T>) = Function(stack(*typeLines), fn(), isRecursive = false)
fun <T> recursive(function: Function<T>) = function.copy(isRecursive = true)
