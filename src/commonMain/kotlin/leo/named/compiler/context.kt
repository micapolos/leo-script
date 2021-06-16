package leo.named.compiler

import leo.Stack
import leo.Type
import leo.doing
import leo.fold
import leo.name
import leo.named.expression.Binding
import leo.named.expression.Expression
import leo.named.expression.binding
import leo.named.expression.expression
import leo.named.typed.TypedExpression
import leo.named.typed.TypedFunction
import leo.named.typed.TypedLine
import leo.named.typed.of
import leo.reverse
import leo.type

data class Context(
	val module: Module,
	val scope: Scope
)

val Module.context: Context get() = Context(this, scope())
fun context(): Context = module().context

fun Context.resolveOrNull(typedExpression: TypedExpression): TypedExpression? =
	module.resolveOrNull(typedExpression)

fun Context.plus(definition: Definition): Context =
	copy(module = module.plus(definition))

fun Context.scopePlus(binding: Binding): Context =
	copy(scope = scope.plus(binding))

fun Context.bind(typedLine: TypedLine): Context =
	plus(typedLine.typeLine.nameDefinition).scopePlus(binding(type(typedLine.typeLine.name), expression(typedLine.line)))

fun Context.bind(typedLineStack: Stack<TypedLine>): Context =
	fold(typedLineStack.reverse) { bind(it) }

fun Context.plusLetDo(typedFunction: TypedFunction): Context =
	plus(typedFunction.typeDoing.definition).scopePlus(binding(typedFunction.typeDoing.lhsType, typedFunction.expression))

fun Context.plus(lhsType: Type, rhsType: Type, function: Expression): Context =
	plusLetDo(function.of(lhsType doing rhsType))
