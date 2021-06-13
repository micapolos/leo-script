package leo.named.compiler

import leo.Type
import leo.base.runIf
import leo.named.expression.Expression
import leo.named.expression.expression
import leo.named.expression.invoke
import leo.named.expression.variable
import leo.named.typed.TypedExpression
import leo.named.typed.typed

data class Binding(val type: Type, val isConstant: Boolean)

fun binding(type: Type, isConstant: Boolean): Binding =
	Binding(type, isConstant)

fun constantBinding(type: Type) =
	binding(type, isConstant = true)

fun functionBinding(type: Type) =
	binding(type, isConstant = false)

fun <T> Binding.resolve(typedExpression: TypedExpression<T>): TypedExpression<T> =
	typed(expression(typedExpression), type)

fun <T> Binding.expression(typedExpression: TypedExpression<T>): Expression<T> =
	expression<T>(variable(typedExpression.type))
		.runIf(!isConstant) { invoke(typedExpression.expression) }