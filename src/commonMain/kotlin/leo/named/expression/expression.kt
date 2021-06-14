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

sealed class Expression { override fun toString() = script.toString() }

data class EmptyExpression(val empty: Empty): Expression() { override fun toString() = super.toString() }
data class LinkExpression(val link: Link): Expression() { override fun toString() = super.toString() }
data class GetExpression(val get: Get): Expression() { override fun toString() = super.toString() }
data class SwitchExpression(val switch: Switch): Expression() { override fun toString() = super.toString() }
data class InvokeExpression(val invoke: Invoke): Expression() { override fun toString() = super.toString() }
data class BindExpression(val bind: Bind): Expression() { override fun toString() = super.toString() }
data class VariableExpression(val variable: Variable): Expression() { override fun toString() = super.toString() }

sealed class Line { override fun toString() = scriptLine.toString() }
data class LiteralLine(val literal: Literal): Line() { override fun toString() = super.toString() }
data class FieldLine(val field: Field): Line() { override fun toString() = super.toString() }
data class FunctionLine(val function: Function): Line() { override fun toString() = super.toString() }
data class AnyLine(val any: Any?): Line() { override fun toString() = super.toString() }

data class Link(val expression: Expression, val line: Line) { override fun toString() = script.toString() }
data class Field(val name: String, val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Get(val expression: Expression, val name: String) { override fun toString() = script.toString() }
data class Switch(val expression: Expression, val cases: Stack<Case>) { override fun toString() = script.toString() }
data class Case(val name: String, val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Function(val body: Body) { override fun toString() = scriptLine.toString() }
data class Invoke(val function: Expression, val params: Expression) { override fun toString() = script.toString() }
data class Bind(val binding: Binding, val expression: Expression) { override fun toString() = script.toString() }
data class Variable(val type: Type) { override fun toString() = script.toString() }
data class Binding(val type: Type, val expression: Expression)

sealed class Body { override fun toString() = super.toString() }
data class ExpressionBody(val expression: Expression): Body() { override fun toString() = script.toString() }
data class FnBody(val name: String, val valueFn: (Dictionary) -> Value): Body() { override fun toString() = script.toString() }

fun expression(empty: Empty): Expression = EmptyExpression(empty)
fun expression(link: Link): Expression = LinkExpression(link)
fun expression(get: Get): Expression = GetExpression(get)
fun expression(switch: Switch): Expression = SwitchExpression(switch)
fun expression(invoke: Invoke): Expression = InvokeExpression(invoke)
fun expression(bind: Bind): Expression = BindExpression(bind)
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
fun body(name: String, fn: (Dictionary) -> Value): Body = FnBody(name, fn)

fun get(expression: Expression, name: String) = Get(expression, name)
fun function(body: Body) = Function(body)
fun invoke(function: Expression, params: Expression) = Invoke(function, params)
fun switch(lhs: Expression, cases: Stack<Case>) = Switch(lhs, cases)
fun variable(type: Type) = Variable(type)
fun bind(binding: Binding, expression: Expression) = Bind(binding, expression)
fun binding(type: Type, expression: Expression) = Binding(type, expression)

fun Expression.get(name: String): Expression = expression(get(this, name))
fun Expression.switch(vararg cases: Case): Expression = expression(switch(this, stack(*cases)))
fun Expression.invoke(expression: Expression): Expression = expression(invoke(this, expression))
fun Binding.in_(expression: Expression): Expression = expression(bind(this, expression))
fun get(type: Type): Expression = expression(variable(type))

fun function(expression: Expression) = expression(line(function(body(expression))))
fun function(name: String, fn: Dictionary.() -> Value) = expression(line(function(body(name, fn))))

fun Switch.expression(name: String): Expression = expressionOrNull(name).notNullOrError("$this.expression($name)")
fun Switch.expressionOrNull(name: String): Expression? = cases.mapFirst { expressionOrNull(name) }
fun Case.expressionOrNull(name: String): Expression? = notNullIf(this.name == name) { expression }

val Expression.linkOrNull: Link? get() = (this as? LinkExpression)?.link