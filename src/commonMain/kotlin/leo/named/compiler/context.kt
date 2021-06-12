package leo.named.compiler

import leo.TypeStructure
import leo.named.typed.TypedExpression
import leo.named.typed.TypedStructure
import leo.named.typed.plus
import leo.named.typed.typedStructure

data class Context<out T>(
	val environment: Environment<T>,
	val dictionary: Dictionary,
	val paramsTuple: TypedStructure<T>
)

val <T> Environment<T>.context get() = Context(this, dictionary(), typedStructure())

fun <T> Context<T>.plus(typeStructure: TypeStructure): Context<T> =
	copy(dictionary = dictionary.plus(typeStructure))

fun <T> Context<T>.plus(typedStructure: TypedStructure<T>): Context<T> =
	copy(dictionary = dictionary.plus(typedStructure.typeStructure))

fun <T> Context<T>.typedExpressionOrNull(typedStructure: TypedStructure<T>): TypedExpression<T>? =
	null // TODO

fun <T> Context<T>.plus(definition: Definition): Context<T> =
	copy(dictionary = dictionary.plus(definition))

fun <T> Context<T>.plusParam(typedExpression: TypedExpression<T>): Context<T> =
	copy(paramsTuple = paramsTuple.plus(typedExpression))
