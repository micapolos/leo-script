package leo.named.compiler

import leo.ChoiceType
import leo.Stack
import leo.StructureType
import leo.Type
import leo.TypeStructure
import leo.fold
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.named.typed.typedExpression
import leo.push
import leo.reverse
import leo.stack

data class Context(
	val dictionary: Dictionary,
	val paramExpressionStack: Stack<TypedExpression>
)

val Dictionary.context: Context get() = Context(this, stack())
fun context(): Context = dictionary().context

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

fun Context.bind(typedLine: TypedLine): Context =
	plus(typedLine.typeLine.nameDefinition).plusParam(typedExpression(typedLine))

fun Context.bind(typedLineStack: Stack<TypedLine>): Context =
	fold(typedLineStack.reverse) { bind(it) }
