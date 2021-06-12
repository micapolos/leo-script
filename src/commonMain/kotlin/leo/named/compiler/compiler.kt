package leo.named.compiler

import leo.isEmpty
import leo.named.expression.expression
import leo.named.expression.function
import leo.named.expression.invoke
import leo.named.typed.TypedExpression
import leo.named.typed.TypedStructure
import leo.named.typed.plus
import leo.named.typed.typed
import leo.named.typed.typedStructure

data class Compiler<out T>(
	val context: Context<T>,
	val bodyTypedStructure: TypedStructure<T>
)

fun <T> Compiler<T>.set(context: Context<T>): Compiler<T> = copy(context = context)
fun <T> Compiler<T>.set(typedStructure: TypedStructure<T>): Compiler<T> = copy(bodyTypedStructure = typedStructure)
val <T> Context<T>.compiler: Compiler<T> get() = Compiler(this, typedStructure())

val <T> Compiler<T>.typedExpression: TypedExpression<T>
	get() =
	bodyTypedStructure.compileOnlyExpression.let { typedExpression ->
		if (context.paramsTuple.typeStructure.lineStack.isEmpty) typedExpression
		else typed(
			expression(
				invoke(
					expression(function(context.paramsTuple.typeStructure, typedExpression.expression)),
					context.paramsTuple.structure)
			),
			typedExpression.typeLine)
	}

fun <T> Compiler<T>.plus(typedExpression: TypedExpression<T>): Compiler<T> =
	set(bodyTypedStructure.plus(typedExpression))

