package leo.indexed.compiler

import leo.indexed.typed.TypedTuple
import leo.indexed.typed.tuple

data class Compiler<out T>(
	val context: Context<T>,
	val tuple: TypedTuple<T>)

fun <T> Compiler<T>.set(context: Context<T>): Compiler<T> = copy(context = context)
fun <T> Compiler<T>.set(tuple: TypedTuple<T>): Compiler<T> = copy(tuple = tuple)
val <T> Context<T>.compiler: Compiler<T> get() = Compiler(this, tuple())