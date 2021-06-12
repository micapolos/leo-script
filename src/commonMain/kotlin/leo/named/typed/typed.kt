package leo.named.typed

import leo.Literal
import leo.TypeLine
import leo.TypeStructure
import leo.base.fold
import leo.lineTo
import leo.named.expression.Expression
import leo.named.expression.Line
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.lineTo
import leo.named.expression.plus
import leo.plus
import leo.type
import leo.typeLine
import leo.typeStructure

data class TypedLine<out T>(val line: Line<T>, val typeLine: TypeLine)
data class TypedExpression<out T>(val expression: Expression<T>, val typeStructure: TypeStructure)

fun <T> typed(line: Line<T>, typeLine: TypeLine) = TypedLine(line, typeLine)
fun <T> typed(expression: Expression<T>, typeStructure: TypeStructure) = TypedExpression(expression, typeStructure)

fun <T> TypedExpression<T>.plus(typedLine: TypedLine<T>): TypedExpression<T> =
	typed(expression.plus(typedLine.line), typeStructure.plus(typedLine.typeLine))

fun <T> typedStructure(vararg typedLine: TypedLine<T>) =
	typed(expression<T>(), typeStructure()).fold(typedLine) { plus(it) }

infix fun <T> String.expressionTo(typedExpression: TypedExpression<T>): TypedLine<T> =
	typed(
		this lineTo typedExpression.expression,
		this lineTo type(typedExpression.typeStructure))

fun <T> typedExpression(literal: Literal): TypedLine<T> =
	typed(expressionLine(literal), literal.typeLine)