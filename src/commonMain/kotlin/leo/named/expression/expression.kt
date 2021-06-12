package leo.named.expression

import leo.Literal
import leo.Stack
import leo.TypeStructure
import leo.base.notNullIf
import leo.base.notNullOrError
import leo.mapFirst
import leo.onlyOrNull
import leo.push
import leo.stack

data class Structure<out T>(val expressionStack: Stack<Expression<T>>)

sealed class Expression<out T>
data class LiteralExpression<T>(val literal: Literal): Expression<T>()
data class FieldExpression<T>(val field: Field<T>): Expression<T>()
data class GetExpression<T>(val get: Get<T>): Expression<T>()
data class SwitchExpression<T>(val switch: Switch<T>): Expression<T>()
data class FunctionExpression<T>(val function: Function<T>): Expression<T>()
data class InvokeExpression<T>(val invoke: Invoke<T>): Expression<T>()
data class VariableExpression<T>(val variable: Variable): Expression<T>()
data class AnyExpression<T>(val any: T): Expression<T>()

data class Field<out T>(val name: String, val structure: Structure<T>)
data class Get<out T>(val expression: Expression<T>, val name: String)
data class Switch<out T>(val lhs: Expression<T>, val cases: Stack<Case<T>>)
data class Case<out T>(val name: String, val expression: Expression<T>)
data class Function<out T>(val paramTypeStructure: TypeStructure, val bodyExpression: Expression<T>)
data class Invoke<out T>(val function: Expression<T>, val params: Structure<T>)
data class Variable(val typeStructure: TypeStructure)

fun <T> expressionStructure(vararg expressions: Expression<T>) = Structure<T>(stack(*expressions))
fun <T> structure(expression: Expression<T>, vararg expressions: Expression<T>) = Structure(stack(expression, *expressions))
infix fun <T> String.fieldTo(rhs: Structure<T>) = Field(this, rhs)
infix fun <T> String.expressionTo(rhs: Structure<T>) = expression(this fieldTo rhs)
infix fun <T> String.caseTo(expression: Expression<T>) = Case(this, expression)

fun <T> expression(literal: Literal): Expression<T> = LiteralExpression(literal)
fun <T> expression(field: Field<T>): Expression<T> = FieldExpression(field)
fun <T> expression(get: Get<T>): Expression<T> = GetExpression(get)
fun <T> expression(switch: Switch<T>): Expression<T> = SwitchExpression(switch)
fun <T> expression(function: Function<T>): Expression<T> = FunctionExpression(function)
fun <T> expression(invoke: Invoke<T>): Expression<T> = InvokeExpression(invoke)
fun <T> expression(variable: Variable): Expression<T> = VariableExpression(variable)
fun <T> anyExpression(any: T): Expression<T> = AnyExpression(any)

fun <T> get(lhs: Expression<T>, name: String) = Get(lhs, name)
fun <T> function(paramTypeStructure: TypeStructure, body: Expression<T>) = Function(paramTypeStructure, body)
fun <T> invoke(function: Expression<T>, params: Structure<T>) = Invoke(function, params)
fun <T> switch(lhs: Expression<T>, cases: Stack<Case<T>>) = Switch(lhs, cases)
fun variable(typeStructure: TypeStructure) = Variable(typeStructure)

fun <T> Expression<T>.get(name: String): Expression<T> = expression(get(this, name))
fun <T> Expression<T>.switch(vararg cases: Case<T>): Expression<T> = expression(switch(this, stack(*cases)))
fun <T> Expression<T>.invoke(structure: Structure<T>): Expression<T> = expression(invoke(this, structure))
fun <T> Expression<T>.invoke(vararg params: Expression<T>): Expression<T> = expression(invoke(this, Structure(stack(*params))))

fun <T> Structure<T>.plus(expression: Expression<T>) = Structure(expressionStack.push(expression))

fun <T> Switch<T>.expression(name: String): Expression<T> = expressionOrNull(name).notNullOrError("$this.expression($name)")
fun <T> Switch<T>.expressionOrNull(name: String): Expression<T>? = cases.mapFirst { expressionOrNull(name) }
fun <T> Case<T>.expressionOrNull(name: String): Expression<T>? = notNullIf(this.name == name) { expression }

fun <T> Structure<T>.get(name: String): Structure<T> =
	structure(expressionStack.onlyOrNull!!.get(name))
