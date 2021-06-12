package leo.named.typed

import leo.Literal
import leo.TypeLine
import leo.TypeStructure
import leo.base.fold
import leo.lineTo
import leo.named.expression.Line
import leo.named.expression.Structure
import leo.named.expression.expressionStructure
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.plus
import leo.plus
import leo.type
import leo.typeLine
import leo.typeStructure

data class TypedExpression<out T>(val line: Line<T>, val typeLine: TypeLine)
data class TypedStructure<out T>(val structure: Structure<T>, val typeStructure: TypeStructure)

fun <T> typed(line: Line<T>, typeLine: TypeLine) = TypedExpression(line, typeLine)
fun <T> typed(structure: Structure<T>, typeStructure: TypeStructure) = TypedStructure(structure, typeStructure)

fun <T> TypedStructure<T>.plus(typedExpression: TypedExpression<T>): TypedStructure<T> =
	typed(structure.plus(typedExpression.line), typeStructure.plus(typedExpression.typeLine))

fun <T> typedStructure(vararg typedExpression: TypedExpression<T>) =
	typed(expressionStructure<T>(), typeStructure()).fold(typedExpression) { plus(it) }

infix fun <T> String.expressionTo(typedStructure: TypedStructure<T>): TypedExpression<T> =
	typed(
		this lineTo typedStructure.structure,
		this lineTo type(typedStructure.typeStructure))

fun <T> typedExpression(literal: Literal): TypedExpression<T> =
	typed(line(literal), literal.typeLine)