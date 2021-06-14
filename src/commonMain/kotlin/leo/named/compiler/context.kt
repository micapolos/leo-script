package leo.named.compiler

import leo.Stack
import leo.Type
import leo.doing
import leo.fold
import leo.named.expression.Expression
import leo.named.expression.expression
import leo.named.typed.TypedExpression
import leo.named.typed.TypedFunction
import leo.named.typed.TypedLine
import leo.named.typed.of
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

fun Context.scopePlus(expression: Expression): Context =
	copy(scope = scope.plus(expression))

fun Context.bind(typedLine: TypedLine): Context =
	plus(typedLine.typeLine.nameDefinition).scopePlus(expression(typedLine.line))

fun Context.bind(typedLineStack: Stack<TypedLine>): Context =
	fold(typedLineStack.reverse) { bind(it) }

fun Context.plusLetDo(typedFunction: TypedFunction): Context =
	plus(typedFunction.typeDoing.definition).scopePlus(typedFunction.exoression)

fun Context.plus(lhsType: Type, rhsType: Type, function: Expression): Context =
	plusLetDo(function.of(lhsType doing rhsType))
