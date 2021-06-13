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

data class TypedLine(val line: Line, val typeLine: TypeLine)
data class TypedExpression(val expression: Expression, val type: Type)

fun typed(line: Line, typeLine: TypeLine) = TypedLine(line, typeLine)
fun typed(expression: Expression, type: Type) = TypedExpression(expression, type)

fun TypedExpression.plus(typedLine: TypedLine): TypedExpression =
	typed(expression.plus(typedLine.line), type.plusOrNull(typedLine.typeLine)!!)

fun typedExpression(vararg typedLine: TypedLine) =
	typed(expression(), type()).fold(typedLine) { plus(it) }

infix fun String.lineTo(typedExpression: TypedExpression): TypedLine =
	typed(
		this lineTo typedExpression.expression,
		this lineTo typedExpression.type)

fun typedExpression(literal: Literal): TypedLine =
	typed(expressionLine(literal), literal.typeLine)