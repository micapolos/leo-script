package leo.named.compiler

import leo.TypeLine
import leo.base.runIf
import leo.named.expression.expression
import leo.named.expression.invoke
import leo.named.expression.variable
import leo.named.typed.TypedExpression
import leo.named.typed.TypedStructure
import leo.named.typed.typed

data class Binding(val typeLine: TypeLine, val isConstant: Boolean)
fun binding(typeLine: TypeLine, isConstant: Boolean): Binding = Binding(typeLine, isConstant)
fun constantBinding(typeLine: TypeLine) = binding(typeLine, isConstant = true)
fun functionBinding(typeLine: TypeLine) = binding(typeLine, isConstant = false)

fun <T> Binding.resolve(typedStructure: TypedStructure<T>): TypedExpression<T> =
	typed(
		expression<T>(variable(typedStructure.typeStructure))
			.runIf(!isConstant) { invoke(typedStructure.structure) },
		typeLine)
