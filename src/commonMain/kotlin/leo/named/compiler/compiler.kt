package leo.named.compiler

import leo.fold
import leo.named.expression.body
import leo.named.expression.expression
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.named.typed.plus
import leo.named.typed.typed
import leo.named.typed.typedExpression

data class Compiler(
	val context: Context,
	val bodyTypedExpression: TypedExpression
)

val Context.compiler: Compiler get() =
	Compiler(this, typedExpression())

fun Compiler.set(context: Context): Compiler =
	copy(context = context)

fun Compiler.set(typedExpression: TypedExpression): Compiler =
	copy(bodyTypedExpression = typedExpression)

val Compiler.typedExpression: TypedExpression
	get() =
		bodyTypedExpression
			.fold(context.scope.typedExpressionStack) { paramTypedExpression ->
				typed(
					expression(
						invoke(
							expression(line(function(body(expression)))),
							paramTypedExpression.expression)
						),
					type)
			}

fun Compiler.plus(typedLine: TypedLine): Compiler =
	set(bodyTypedExpression.plus(typedLine))

