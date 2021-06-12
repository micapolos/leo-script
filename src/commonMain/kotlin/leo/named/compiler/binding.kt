package leo.named.compiler

import leo.Type
import leo.base.runIf
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.expression.variable
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.named.typed.typed
import leo.onlyLineOrNull

data class Binding(val type: Type, val isConstant: Boolean)
fun binding(type: Type, isConstant: Boolean): Binding = Binding(type, isConstant)
fun constantBinding(type: Type) = binding(type, isConstant = true)
fun functionBinding(type: Type) = binding(type, isConstant = false)

fun <T> Binding.resolve(typedExpression: TypedExpression<T>): TypedLine<T> =
	typed(
		line<T>(variable(typedExpression.typeStructure))
			.runIf(!isConstant) { invoke(typedExpression.expression) },
		type.onlyLineOrNull!!) // TODO()
