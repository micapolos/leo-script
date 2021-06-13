package leo.named.compiler

import leo.ChoiceType
import leo.Stack
import leo.StructureType
import leo.Type
import leo.TypeStructure
import leo.named.typed.TypedExpression
import leo.push
import leo.stack

data class Context<out T>(
	val dictionary: Dictionary,
	val paramExpressionStack: Stack<TypedExpression<T>>
)

fun <T> Dictionary.context(): Context<T> = Context(this, stack())
fun <T> context(): Context<T> = dictionary().context()

fun <T> Context<T>.plusNames(type: Type): Context<T> =
	// TODO: Add "content"
	when (type) {
		is ChoiceType -> this
		is StructureType -> plusNames(type.structure)
	}

fun <T> Context<T>.plusNames(typeStructure: TypeStructure): Context<T> =
	copy(dictionary = dictionary.plusNames(typeStructure))

fun <T> Context<T>.resolveOrNull(typedExpression: TypedExpression<T>): TypedExpression<T>? =
	dictionary.resolveOrNull(typedExpression)

fun <T> Context<T>.plus(definition: Definition): Context<T> =
	copy(dictionary = dictionary.plus(definition))

fun <T> Context<T>.plusParam(typedLine: TypedExpression<T>): Context<T> =
	copy(paramExpressionStack = paramExpressionStack.push(typedLine))
