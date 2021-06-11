package leo.indexed.compiler

import leo.TypeStructure
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.plus
import leo.indexed.typed.tuple
import leo.indexed.typed.type

data class Context<out T>(
	val environment: Environment<T>,
	val dictionary: Dictionary,
	val paramsTuple: TypedTuple<T>)

val <T> Environment<T>.context get() = Context(this, dictionary(), tuple())

fun <T> Context<T>.plus(typeStructure: TypeStructure): Context<T> =
	copy(dictionary = dictionary.plus(typeStructure))

fun <T> Context<T>.plus(tuple: TypedTuple<T>): Context<T> =
	copy(dictionary = dictionary.plus(tuple.type.compileStructure))

fun <T> Context<T>.typedOrNull(tuple: TypedTuple<T>): Typed<T>? =
	dictionary.indexedBindingOrNull(tuple.type.compileStructure)?.apply(tuple)

fun <T> Context<T>.plus(definition: Definition): Context<T> =
	copy(dictionary = dictionary.plus(definition))

fun <T> Context<T>.plusParam(typed: Typed<T>): Context<T> =
	copy(paramsTuple = paramsTuple.plus(typed))
