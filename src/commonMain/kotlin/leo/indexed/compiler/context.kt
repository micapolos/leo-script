package leo.indexed.compiler

import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.type

data class Context<out T>(
	val environment: Environment<T>,
	val dictionary: Dictionary)

val <T> Environment<T>.context get() = Context(this, dictionary())

fun <T> Context<T>.plus(tuple: TypedTuple<T>): Context<T> =
	copy(dictionary = dictionary.plus(tuple.type.compileStructure))

fun <T> Context<T>.typedOrNull(tuple: TypedTuple<T>): Typed<T>? =
	dictionary.indexedBindingOrNull(tuple.type.compileStructure)?.apply(tuple)