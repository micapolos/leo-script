package leo.named.compiler

import leo.ChoiceType
import leo.Stack
import leo.StructureType
import leo.Type
import leo.TypeStructure
import leo.named.typed.TypedExpression
import leo.push
import leo.stack

data class Context(
	val dictionary: Dictionary,
	val paramExpressionStack: Stack<TypedExpression>
)

fun Dictionary.context(): Context = Context(this, stack())
fun context(): Context = dictionary().context()

fun Context.plusNames(type: Type): Context =
	// TODO: Add "content"
	when (type) {
		is ChoiceType -> this
		is StructureType -> plusNames(type.structure)
	}

fun Context.plusNames(typeStructure: TypeStructure): Context =
	copy(dictionary = dictionary.plusNames(typeStructure))

fun Context.resolveOrNull(typedExpression: TypedExpression): TypedExpression? =
	dictionary.resolveOrNull(typedExpression)

fun Context.plus(definition: Definition): Context =
	copy(dictionary = dictionary.plus(definition))

fun Context.plusParam(typedLine: TypedExpression): Context =
	copy(paramExpressionStack = paramExpressionStack.push(typedLine))
