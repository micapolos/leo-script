package leo.named.compiler

import leo.Type
import leo.base.runIf
import leo.named.expression.Expression
import leo.named.expression.expression
import leo.named.expression.invoke
import leo.named.expression.variable
import leo.named.typed.TypedExpression
import leo.named.typed.typed

data class Binding(val type: Type, val isConstant: Boolean) { override fun toString() = scriptLine.toString() }

fun binding(type: Type, isConstant: Boolean): Binding =
	Binding(type, isConstant)

fun constantBinding(type: Type) =
	binding(type, isConstant = true)

fun functionBinding(type: Type) =
	binding(type, isConstant = false)

fun Binding.resolve(typedExpression: TypedExpression): TypedExpression =
	typed(expression(typedExpression), type)

fun Binding.expression(typedExpression: TypedExpression): Expression =
	expression(variable(typedExpression.type))
		.runIf(!isConstant) { invoke(typedExpression.expression) }