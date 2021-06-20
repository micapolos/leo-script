package leo.named.compiler

import leo.Type
import leo.named.expression.Expression
import leo.named.expression.invoke
import leo.named.typed.TypedExpression
import leo.named.typed.of

data class Binding(val type: Type, val isConstant: Boolean) { override fun toString() = scriptLine.toString() }

fun binding(type: Type, isConstant: Boolean): Binding =
	Binding(type, isConstant)

fun constantBinding(type: Type) =
	binding(type, isConstant = true)

fun functionBinding(type: Type) =
	binding(type, isConstant = false)

fun Binding.resolve(typedExpression: TypedExpression): TypedExpression =
	expression(typedExpression).of(type)

fun Binding.expression(typedExpression: TypedExpression): Expression =
	typedExpression.expression.invoke(typedExpression.type)
		//.runIf(!isConstant) { give(typedExpression.expression) }