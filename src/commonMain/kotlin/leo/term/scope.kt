package leo.term

import leo.Stack
import leo.get
import leo.push
import leo.stack

data class Scope<out T>(val stack: Stack<Value<T>>) { override fun toString() = scriptLine.toString() }

fun <T> scope(vararg values: Value<T>) =
	Scope(stack(*values))

fun <T> Scope<T>.plus(value: Value<T>): Scope<T> =
	stack.push(value).let(::Scope)

fun <T> Scope<T>.value(variable: TermVariable): Value<T> =
	stack.get(variable.index)!!