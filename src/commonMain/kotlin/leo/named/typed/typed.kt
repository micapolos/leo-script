package leo.named.typed

import leo.Literal
import leo.TypeLine
import leo.TypeStructure
import leo.base.fold
import leo.lineTo
import leo.named.Expression
import leo.named.Structure
import leo.named.expression
import leo.named.expressionTo
import leo.named.plus
import leo.named.structure
import leo.plus
import leo.type
import leo.typeLine
import leo.typeStructure

data class TypedExpression<out T>(val expression: Expression<T>, val typeLine: TypeLine)
data class TypedStructure<out T>(val structure: Structure<T>, val typeStructure: TypeStructure)

fun <T> typed(expression: Expression<T>, typeLine: TypeLine) = TypedExpression(expression, typeLine)
fun <T> typed(structure: Structure<T>, typeStructure: TypeStructure) = TypedStructure(structure, typeStructure)

fun <T> TypedStructure<T>.plus(typedExpression: TypedExpression<T>): TypedStructure<T> =
	typed(structure.plus(typedExpression.expression), typeStructure.plus(typedExpression.typeLine))

fun <T> typedStructure(vararg typedExpression: TypedExpression<T>) =
	typed(structure<T>(), typeStructure()).fold(typedExpression) { plus(it) }

infix fun <T> String.expressionTo(typedStructure: TypedStructure<T>): TypedExpression<T> =
	typed(
		this expressionTo typedStructure.structure,
		this lineTo type(typedStructure.typeStructure))

fun <T> typedExpression(literal: Literal): TypedExpression<T> =
	typed(expression(literal), literal.typeLine)