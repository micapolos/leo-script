package leo.named.typed

import leo.Literal
import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.TypeDoing
import leo.TypeField
import leo.TypeLine
import leo.all
import leo.atom
import leo.base.fold
import leo.doingLineTo
import leo.fieldTo
import leo.line
import leo.lineTo
import leo.map
import leo.name
import leo.named.compiler.check
import leo.named.compiler.checkType
import leo.named.compiler.compileDoing
import leo.named.compiler.get
import leo.named.compiler.switchChoice
import leo.named.expression.Case
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
import leo.named.expression.switch
import leo.named.expression.with
import leo.plus
import leo.script
import leo.type
import leo.typeLine
import leo.zip

data class TypedLine(val line: Line, val typeLine: TypeLine)
data class TypedExpression(val expression: Expression, val type: Type) { override fun toString() = script.toString() }
data class TypedField(val field: Field, val typeField: TypeField)
data class TypedFunction(val expression: Expression, val typeDoing: TypeDoing)
data class TypedChoice(val line: Line, val typeChoice: TypeChoice)
data class TypedCase(val case: Case, val type: Type)

fun typed(line: Line, typeLine: TypeLine) = TypedLine(line, typeLine)
fun typed(expression: Expression, type: Type) = TypedExpression(expression, type)
fun typed(field: Field, typeField: TypeField) = TypedField(field, typeField)
fun typed(line: Line, typeChoice: TypeChoice) = TypedChoice(line, typeChoice)
fun typed(case: Case, type: Type) = TypedCase(case, type)

infix fun Expression.of(type: Type) = typed(this, type)
infix fun Expression.of(typeDoing: TypeDoing) = TypedFunction(this, typeDoing)

fun TypedExpression.plus(typedLine: TypedLine): TypedExpression =
	type.plus(typedLine.typeLine).let { expression.plus(typedLine.line).of(it) }

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
	function(typedExpression.expression).invoke(expression).of(typedExpression.type)

fun TypedExpression.invoke(typedExpression: TypedExpression): TypedExpression =
	type.compileDoing.let { doing ->
		doing.lhsType.check(typedExpression.type) {
			expression.invoke(typedExpression.expression).of(doing.rhsType)
		}
	}

fun TypedExpression.switch(casesStack: Stack<TypedCase>): TypedExpression =
	zip(type.switchChoice.lineStack, casesStack)
		.map {
			if (first == null) error("exhausted")
			else if (second == null) error("not exhaustive")
			else if (first!!.name != second!!.case.name) error("case mismatch")
			else true
		}
		.all { this }
		.run { if (!this) error("dupa") }
		.run {
			checkType(casesStack.map { type }).let { type ->
				typed(expression.switch(casesStack.map { case }), type)
			}
		}

@Suppress("ComplexRedundantLet") // Type.get(name) must be evaluated first.
fun TypedExpression.get(name: String): TypedExpression =
	type.get(name).let {
		expression.get(name).of(it)
	}

infix fun Type.doingTypedLine(bodyTypedExpression: TypedExpression): TypedLine =
	typed(
		line(function(body(bodyTypedExpression.expression))),
		this.doingLineTo(bodyTypedExpression.type))

fun TypedExpression.with(typedExpression: TypedExpression): TypedExpression =
	typed(expression.with(typedExpression.expression), type.plus(typedExpression.type))

val TypedField.name: String get() = typeField.name
val TypedField.line: TypedLine get() = typed(line(field), line(atom(typeField)))
val TypedField.rhs: TypedExpression get() = field.expression.of(typeField.rhsType)

val Type.typedExpression: TypedExpression get() = script.reflectTypedExpression