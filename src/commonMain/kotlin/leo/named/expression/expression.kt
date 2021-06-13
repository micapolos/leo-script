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

sealed class Expression<out T>
data class EmptyExpression<T>(val empty: Empty): Expression<T>()
data class LinkExpression<T>(val link: Link<T>): Expression<T>()
data class GetExpression<T>(val get: Get<T>): Expression<T>()
data class SwitchExpression<T>(val switch: Switch<T>): Expression<T>()
data class InvokeExpression<T>(val invoke: Invoke<T>): Expression<T>()
data class VariableExpression<T>(val variable: Variable): Expression<T>()

sealed class Line<out T>
data class LiteralLine<T>(val literal: Literal): Line<T>()
data class FieldLine<T>(val field: Field<T>): Line<T>()
data class FunctionLine<T>(val function: Function<T>): Line<T>()
data class AnyLine<T>(val any: T): Line<T>()

data class Link<out T>(val expression: Expression<T>, val line: Line<T>)
data class Field<out T>(val name: String, val expression: Expression<T>)
data class Get<out T>(val expression: Expression<T>, val name: String)
data class Switch<out T>(val expression: Expression<T>, val cases: Stack<Case<T>>)
data class Case<out T>(val name: String, val line: Expression<T>)
data class Function<out T>(val paramType: Type, val body: Body<T>)
data class Invoke<out T>(val function: Expression<T>, val params: Expression<T>)
data class Variable(val type: Type)

sealed class Body<out T>
data class ExpressionBody<T>(val expression: Expression<T>): Body<T>()
data class FnBody<T>(val valueFn: (Dictionary<T>) -> Value<T>): Body<T>()

fun <T> expression(empty: Empty): Expression<T> = EmptyExpression(empty)
fun <T> expression(link: Link<T>): Expression<T> = LinkExpression(link)
fun <T> expression(get: Get<T>): Expression<T> = GetExpression(get)
fun <T> expression(switch: Switch<T>): Expression<T> = SwitchExpression(switch)
fun <T> expression(invoke: Invoke<T>): Expression<T> = InvokeExpression(invoke)
fun <T> expression(variable: Variable): Expression<T> = VariableExpression(variable)

fun <T> Expression<T>.plus(line: Line<T>) = expression(this linkTo line)
fun <T> expression(vararg lines: Line<T>): Expression<T> = expression<T>(empty).fold(lines) { plus(it) }

infix fun <T> String.fieldTo(rhs: Expression<T>) = Field(this, rhs)
infix fun <T> String.lineTo(rhs: Expression<T>) = line(this fieldTo rhs)
infix fun <T> String.caseTo(line: Expression<T>) = Case(this, line)
infix fun <T> Expression<T>.linkTo(line: Line<T>) = Link(this, line)

fun <T> expressionLine(literal: Literal): Line<T> = LiteralLine(literal)
fun <T> line(field: Field<T>): Line<T> = FieldLine(field)
fun <T> line(function: Function<T>): Line<T> = FunctionLine(function)
fun <T> anyExpressionLine(any: T): Line<T> = AnyLine(any)

fun <T> body(expression: Expression<T>): Body<T> = ExpressionBody(expression)
fun <T> body(fn: (Dictionary<T>) -> Value<T>): Body<T> = FnBody(fn)

fun <T> get(expression: Expression<T>, name: String) = Get(expression, name)
fun <T> function(paramType: Type, body: Body<T>) = Function(paramType, body)
fun <T> invoke(function: Expression<T>, params: Expression<T>) = Invoke(function, params)
fun <T> switch(lhs: Expression<T>, cases: Stack<Case<T>>) = Switch(lhs, cases)
fun variable(type: Type) = Variable(type)

fun <T> Expression<T>.get(name: String): Expression<T> = expression(get(this, name))
fun <T> Expression<T>.switch(vararg cases: Case<T>): Expression<T> = expression(switch(this, stack(*cases)))
fun <T> Expression<T>.invoke(expression: Expression<T>): Expression<T> = expression(invoke(this, expression))

fun <T> Switch<T>.expression(name: String): Expression<T> = expressionOrNull(name).notNullOrError("$this.expression($name)")
fun <T> Switch<T>.expressionOrNull(name: String): Expression<T>? = cases.mapFirst { expressionOrNull(name) }
fun <T> Case<T>.expressionOrNull(name: String): Expression<T>? = notNullIf(this.name == name) { line }

val <T> Expression<T>.linkOrNull: Link<T>? get() = (this as? LinkExpression)?.link