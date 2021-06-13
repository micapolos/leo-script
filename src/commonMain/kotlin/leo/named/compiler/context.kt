package leo.named.compiler

import leo.Stack
import leo.Type
import leo.fold
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.named.typed.typedExpression
import leo.reverse

data class Context(
	val dictionary: Dictionary,
	val scope: Scope
)

val Dictionary.context: Context get() = Context(this, scope())
fun context(): Context = dictionary().context

fun Context.plusNames(type: Type): Context =
	copy(dictionary = dictionary.plusNames(type))

fun Context.resolveOrNull(typedExpression: TypedExpression): TypedExpression? =
	dictionary.resolveOrNull(typedExpression)

fun Context.plus(definition: Definition): Context =
	copy(dictionary = dictionary.plus(definition))

fun Context.scopePlus(typedExpression: TypedExpression): Context =
	copy(scope = scope.plus(typedExpression))

fun Context.bind(typedLine: TypedLine): Context =
	plus(typedLine.typeLine.nameDefinition).scopePlus(typedExpression(typedLine))

fun Context.bind(typedLineStack: Stack<TypedLine>): Context =
	fold(typedLineStack.reverse) { bind(it) }
