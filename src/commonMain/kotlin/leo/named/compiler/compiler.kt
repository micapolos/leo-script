package leo.named.compiler

import leo.fold
import leo.named.expression.expression
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.named.typed.plus
import leo.named.typed.typed
import leo.named.typed.typedExpression

data class Compiler<out T>(
	val context: Context<T>,
	val bodyTypedExpression: TypedExpression<T>
)

fun <T> Compiler<T>.set(context: Context<T>): Compiler<T> = copy(context = context)
fun <T> Compiler<T>.set(typedExpression: TypedExpression<T>): Compiler<T> = copy(bodyTypedExpression = typedExpression)
val <T> Context<T>.compiler: Compiler<T> get() = Compiler(this, typedExpression())

val <T> Compiler<T>.typedExpression: TypedExpression<T>
	get() =
		bodyTypedExpression
			.fold(context.paramExpressionStack) { paramTypedExpression ->
				typed(
					expression(
						line(
							invoke(
								line(function(paramTypedExpression.type, expression)),
								paramTypedExpression.expression)
							)
						),
					type)
			}

fun <T> Compiler<T>.plus(typedLine: TypedLine<T>): Compiler<T> =
	set(bodyTypedExpression.plus(typedLine))

