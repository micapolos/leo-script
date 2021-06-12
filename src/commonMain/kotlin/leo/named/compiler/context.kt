package leo.named.compiler

import leo.Stack
import leo.TypeStructure
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.push
import leo.stack

data class Context<out T>(
	val environment: Environment<T>,
	val dictionary: Dictionary,
	val paramLineStack: Stack<TypedLine<T>>
)

val <T> Environment<T>.context get() = Context(this, dictionary(), stack())

fun <T> Context<T>.plus(typeStructure: TypeStructure): Context<T> =
	copy(dictionary = dictionary.plusNames(typeStructure))

fun <T> Context<T>.resolveOrNull(typedExpression: TypedExpression<T>): TypedLine<T>? =
	dictionary.resolveOrNull(typedExpression)

fun <T> Context<T>.plus(definition: Definition): Context<T> =
	copy(dictionary = dictionary.plus(definition))

fun <T> Context<T>.plusParam(typedLine: TypedLine<T>): Context<T> =
	copy(paramLineStack = paramLineStack.push(typedLine))
