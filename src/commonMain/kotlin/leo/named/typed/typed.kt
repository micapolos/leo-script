package leo.named.typed

import leo.Literal
import leo.Type
import leo.TypeField
import leo.TypeLine
import leo.atom
import leo.base.fold
import leo.doingLineTo
import leo.fieldTo
import leo.line
import leo.lineTo
import leo.named.compiler.check
import leo.named.compiler.compileDoing
import leo.named.compiler.get
import leo.named.expression.Expression
import leo.named.expression.Field
import leo.named.expression.Line
import leo.named.expression.body
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.fieldTo
import leo.named.expression.function
import leo.named.expression.get
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.plus
import leo.plusOrNull
import leo.script
import leo.type
import leo.typeLine

data class TypedLine(val line: Line, val typeLine: TypeLine)
data class TypedExpression(val expression: Expression, val type: Type) { override fun toString() = script.toString() }
data class TypedField(val field: Field, val typeField: TypeField)

fun typed(line: Line, typeLine: TypeLine) = TypedLine(line, typeLine)
fun typed(expression: Expression, type: Type) = TypedExpression(expression, type)
fun typed(field: Field, typeField: TypeField) = TypedField(field, typeField)

fun TypedExpression.plus(typedLine: TypedLine): TypedExpression =
	typed(expression.plus(typedLine.line), type.plusOrNull(typedLine.typeLine)!!)

fun typedExpression(vararg typedLine: TypedLine) =
	typed(expression(), type()).fold(typedLine) { plus(it) }

infix fun String.lineTo(typedExpression: TypedExpression): TypedLine =
	typed(
		this lineTo typedExpression.expression,
		this lineTo typedExpression.type)

infix fun String.fieldTo(typedExpression: TypedExpression): TypedField =
	typed(
		this fieldTo typedExpression.expression,
		this fieldTo typedExpression.type)

fun typedLine(literal: Literal): TypedLine =
	typed(expressionLine(literal), literal.typeLine)

fun TypedExpression.do_(typedExpression: TypedExpression): TypedExpression =
	typed(
		expression(
			invoke(
				function(typedExpression.expression),
				expression
			)
		),
		typedExpression.type)

fun TypedExpression.invoke(typedExpression: TypedExpression): TypedExpression =
	type.compileDoing.let { doing ->
		doing.lhsType.check(typedExpression.type) {
			typed(
				expression.invoke(typedExpression.expression),
				doing.rhsType)
		}
	}

@Suppress("ComplexRedundantLet") // Type.get(name) must be evaluated first.
fun TypedExpression.get(name: String): TypedExpression =
	type.get(name).let {
		typed(expression.get(name), it)
	}

infix fun Type.doingTypedLine(bodyTypedExpression: TypedExpression): TypedLine =
	typed(
		line(function(body(bodyTypedExpression.expression))),
		this.doingLineTo(bodyTypedExpression.type))

val TypedField.name: String get() = typeField.name
val TypedField.line: TypedLine get() = typed(line(field), line(atom(typeField)))
val TypedField.rhs: TypedExpression get() = typed(field.expression, typeField.rhsType)

val Type.typedExpression: TypedExpression get() = script.reflectTypedExpression