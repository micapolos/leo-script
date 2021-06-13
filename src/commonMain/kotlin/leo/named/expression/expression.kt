package leo.named.expression

import leo.Empty
import leo.Literal
import leo.Stack
import leo.Type
import leo.base.fold
import leo.base.notNullIf
import leo.base.notNullOrError
import leo.empty
import leo.mapFirst
import leo.named.evaluator.Dictionary
import leo.named.value.Value
import leo.stack

sealed class Expression
data class EmptyExpression(val empty: Empty): Expression()
data class LinkExpression(val link: Link): Expression()
data class GetExpression(val get: Get): Expression()
data class SwitchExpression(val switch: Switch): Expression()
data class InvokeExpression(val invoke: Invoke): Expression()
data class VariableExpression(val variable: Variable): Expression()

sealed class Line
data class LiteralLine(val literal: Literal): Line()
data class FieldLine(val field: Field): Line()
data class FunctionLine(val function: Function): Line()
data class AnyLine(val any: Any?): Line()

data class Link(val expression: Expression, val line: Line)
data class Field(val name: String, val expression: Expression)
data class Get(val expression: Expression, val name: String)
data class Switch(val expression: Expression, val cases: Stack<Case>)
data class Case(val name: String, val line: Expression)
data class Function(val paramType: Type, val body: Body)
data class Invoke(val function: Expression, val params: Expression)
data class Variable(val type: Type)

sealed class Body
data class ExpressionBody(val expression: Expression): Body()
data class FnBody(val valueFn: (Dictionary) -> Value): Body()

fun expression(empty: Empty): Expression = EmptyExpression(empty)
fun expression(link: Link): Expression = LinkExpression(link)
fun expression(get: Get): Expression = GetExpression(get)
fun expression(switch: Switch): Expression = SwitchExpression(switch)
fun expression(invoke: Invoke): Expression = InvokeExpression(invoke)
fun expression(variable: Variable): Expression = VariableExpression(variable)

fun Expression.plus(line: Line) = expression(this linkTo line)
fun expression(vararg lines: Line): Expression = expression(empty).fold(lines) { plus(it) }

infix fun String.fieldTo(rhs: Expression) = Field(this, rhs)
infix fun String.lineTo(rhs: Expression) = line(this fieldTo rhs)
infix fun String.caseTo(line: Expression) = Case(this, line)
infix fun Expression.linkTo(line: Line) = Link(this, line)

fun expressionLine(literal: Literal): Line = LiteralLine(literal)
fun line(field: Field): Line = FieldLine(field)
fun line(function: Function): Line = FunctionLine(function)
fun anyExpressionLine(any: Any?): Line = AnyLine(any)

fun body(expression: Expression): Body = ExpressionBody(expression)
fun body(fn: (Dictionary) -> Value): Body = FnBody(fn)

fun get(expression: Expression, name: String) = Get(expression, name)
fun function(paramType: Type, body: Body) = Function(paramType, body)
fun invoke(function: Expression, params: Expression) = Invoke(function, params)
fun switch(lhs: Expression, cases: Stack<Case>) = Switch(lhs, cases)
fun variable(type: Type) = Variable(type)

fun Expression.get(name: String): Expression = expression(get(this, name))
fun Expression.switch(vararg cases: Case): Expression = expression(switch(this, stack(*cases)))
fun Expression.invoke(expression: Expression): Expression = expression(invoke(this, expression))

fun Switch.expression(name: String): Expression = expressionOrNull(name).notNullOrError("$this.expression($name)")
fun Switch.expressionOrNull(name: String): Expression? = cases.mapFirst { expressionOrNull(name) }
fun Case.expressionOrNull(name: String): Expression? = notNullIf(this.name == name) { line }

val Expression.linkOrNull: Link? get() = (this as? LinkExpression)?.link