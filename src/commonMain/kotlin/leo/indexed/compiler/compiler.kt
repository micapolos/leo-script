package leo.indexed.compiler

import leo.indexed.expression
import leo.indexed.function
import leo.indexed.invoke
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.expressionTuple
import leo.indexed.typed.plus
import leo.indexed.typed.tuple
import leo.indexed.typed.typed
import leo.isEmpty
import leo.size

data class Compiler<out T>(
	val context: Context<T>,
	val bodyTuple: TypedTuple<T>)

fun <T> Compiler<T>.set(context: Context<T>): Compiler<T> = copy(context = context)
fun <T> Compiler<T>.set(tuple: TypedTuple<T>): Compiler<T> = copy(bodyTuple = tuple)
val <T> Context<T>.compiler: Compiler<T> get() = Compiler(this, tuple())

val <T> Compiler<T>.typed: Typed<T> get() =
	bodyTuple.compileTyped.let { typed ->
		if (context.paramsTuple.typedStack.isEmpty) typed
		else typed(
			expression(
				invoke(
					expression(function(context.paramsTuple.typedStack.size, typed.expression)),
					context.paramsTuple.expressionTuple)),
			typed.typeLine)
	}

fun <T> Compiler<T>.plus(typed: Typed<T>): Compiler<T> =
	set(bodyTuple.plus(typed))

