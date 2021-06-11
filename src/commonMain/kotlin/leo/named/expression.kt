package leo.named

import leo.Literal
import leo.Stack
import leo.TypeStructure
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
data class AnyExpression<T>(val any: Any?): Expression<T>()

data class Field<out T>(val name: String, val rhs: Structure<T>)
data class Get<out T>(val lhs: Expression<T>, val name: String)
data class Switch<out T>(val lhs: Expression<T>, val cases: Stack<Field<T>>)
data class Function<out T>(val paramCount: Int, val body: Expression<T>)
data class Invoke<out T>(val function: Expression<T>, val params: Structure<T>)
data class Variable(val typeStructure: TypeStructure)

fun <T> structure(vararg expressions: Expression<T>) = Structure(stack(*expressions))
infix fun <T> String.fieldTo(rhs: Structure<T>) = Field(this, rhs)
infix fun <T> String.expressionTo(rhs: Structure<T>) = expression(this fieldTo rhs)

fun <T> expression(literal: Literal): Expression<T> = LiteralExpression(literal)
fun <T> expression(field: Field<T>): Expression<T> = FieldExpression(field)
fun <T> expression(get: Get<T>): Expression<T> = GetExpression(get)
fun <T> expression(switch: Switch<T>): Expression<T> = SwitchExpression(switch)
fun <T> expression(function: Function<T>): Expression<T> = FunctionExpression(function)
fun <T> expression(invoke: Invoke<T>): Expression<T> = InvokeExpression(invoke)
fun <T> expression(variable: Variable): Expression<T> = VariableExpression(variable)
fun <T> anyExpression(any: T): Expression<T> = AnyExpression(any)

fun <T> get(lhs: Expression<T>, name: String) = Get(lhs, name)
fun <T> function(paramCount: Int, body: Expression<T>) = Function(paramCount, body)
fun <T> invoke(function: Expression<T>, params: Structure<T>) = Invoke(function, params)
fun <T> switch(lhs: Expression<T>, cases: Stack<Field<T>>) = Switch(lhs, cases)
fun variable(typeStructure: TypeStructure) = Variable(typeStructure)

fun <T> Expression<T>.get(name: String): Expression<T> = expression(get(this, name))
fun <T> Expression<T>.switch(vararg fields: Field<T>): Expression<T> = expression(switch(this, stack(*fields)))
fun <T> Expression<T>.invoke(vararg params: Expression<T>): Expression<T> = expression(invoke(this, structure(*params)))

fun <T> Structure<T>.plus(expression: Expression<T>) = Structure(expressionStack.push(expression))