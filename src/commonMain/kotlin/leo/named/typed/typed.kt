package leo.named.typed

import leo.Literal
import leo.Type
import leo.TypeLine
import leo.base.fold
import leo.lineTo
import leo.named.expression.Expression
import leo.named.expression.Line
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.lineTo
import leo.named.expression.plus
import leo.plusOrNull
import leo.type
import leo.typeLine

data class TypedLine<out T>(val line: Line<T>, val typeLine: TypeLine)
data class TypedExpression<out T>(val expression: Expression<T>, val type: Type)

fun <T> typed(line: Line<T>, typeLine: TypeLine) = TypedLine(line, typeLine)
fun <T> typed(expression: Expression<T>, type: Type) = TypedExpression(expression, type)

fun <T> TypedExpression<T>.plus(typedLine: TypedLine<T>): TypedExpression<T> =
	typed(expression.plus(typedLine.line), type.plusOrNull(typedLine.typeLine)!!)

fun <T> typedExpression(vararg typedLine: TypedLine<T>) =
	typed(expression<T>(), type()).fold(typedLine) { plus(it) }

infix fun <T> String.lineTo(typedExpression: TypedExpression<T>): TypedLine<T> =
	typed(
		this lineTo typedExpression.expression,
		this lineTo typedExpression.type)

fun <T> typedExpression(literal: Literal): TypedLine<T> =
	typed(expressionLine(literal), literal.typeLine)