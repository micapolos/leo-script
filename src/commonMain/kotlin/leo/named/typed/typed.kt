package leo.named.typed

import leo.Literal
import leo.TypeLine
import leo.TypeStructure
import leo.base.fold
import leo.lineTo
import leo.named.expression.Expression
import leo.named.expression.Line
import leo.named.expression.expression
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.plus
import leo.plus
import leo.type
import leo.typeLine
import leo.typeStructure

data class TypedExpression<out T>(val line: Line<T>, val typeLine: TypeLine)
data class TypedStructure<out T>(val expression: Expression<T>, val typeStructure: TypeStructure)

fun <T> typed(line: Line<T>, typeLine: TypeLine) = TypedExpression(line, typeLine)
fun <T> typed(expression: Expression<T>, typeStructure: TypeStructure) = TypedStructure(expression, typeStructure)

fun <T> TypedStructure<T>.plus(typedExpression: TypedExpression<T>): TypedStructure<T> =
	typed(expression.plus(typedExpression.line), typeStructure.plus(typedExpression.typeLine))

fun <T> typedStructure(vararg typedExpression: TypedExpression<T>) =
	typed(expression<T>(), typeStructure()).fold(typedExpression) { plus(it) }

infix fun <T> String.expressionTo(typedStructure: TypedStructure<T>): TypedExpression<T> =
	typed(
		this lineTo typedStructure.expression,
		this lineTo type(typedStructure.typeStructure))

fun <T> typedExpression(literal: Literal): TypedExpression<T> =
	typed(line(literal), literal.typeLine)