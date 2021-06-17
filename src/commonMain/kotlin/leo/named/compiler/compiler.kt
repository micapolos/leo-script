package leo.named.compiler

import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.named.typed.plus
import leo.named.typed.typedExpression

data class Compiler(
	val module: Module,
	val bodyTypedExpression: TypedExpression
)

val Module.compiler: Compiler get() =
	Compiler(this, typedExpression())

fun Compiler.set(module: Module): Compiler =
	copy(module = module)

fun Compiler.set(typedExpression: TypedExpression): Compiler =
	copy(bodyTypedExpression = typedExpression)

val Compiler.typedExpression: TypedExpression
	get() =
		module.privateContext.scope.in_(bodyTypedExpression)

fun Compiler.plus(typedLine: TypedLine): Compiler =
	set(bodyTypedExpression.plus(typedLine))

