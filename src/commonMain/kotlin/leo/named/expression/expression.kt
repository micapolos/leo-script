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

data class Structure<out T>(val lineStack: Stack<Line<T>>)

sealed class Line<out T>
data class LiteralLine<T>(val literal: Literal): Line<T>()
data class FieldLine<T>(val field: Field<T>): Line<T>()
data class GetLine<T>(val get: Get<T>): Line<T>()
data class SwitchLine<T>(val switch: Switch<T>): Line<T>()
data class FunctionLine<T>(val function: Function<T>): Line<T>()
data class InvokeLine<T>(val invoke: Invoke<T>): Line<T>()
data class VariableLine<T>(val variable: Variable): Line<T>()
data class AnyLine<T>(val any: T): Line<T>()

data class Field<out T>(val name: String, val structure: Structure<T>)
data class Get<out T>(val line: Line<T>, val name: String)
data class Switch<out T>(val lhs: Line<T>, val cases: Stack<Case<T>>)
data class Case<out T>(val name: String, val line: Line<T>)
data class Function<out T>(val paramTypeStructure: TypeStructure, val bodyLine: Line<T>)
data class Invoke<out T>(val function: Line<T>, val params: Structure<T>)
data class Variable(val typeStructure: TypeStructure)

fun <T> expressionStructure(vararg lines: Line<T>) = Structure<T>(stack(*lines))
fun <T> structure(line: Line<T>, vararg lines: Line<T>) = Structure(stack(line, *lines))
infix fun <T> String.fieldTo(rhs: Structure<T>) = Field(this, rhs)
infix fun <T> String.lineTo(rhs: Structure<T>) = line(this fieldTo rhs)
infix fun <T> String.caseTo(line: Line<T>) = Case(this, line)

fun <T> line(literal: Literal): Line<T> = LiteralLine(literal)
fun <T> line(field: Field<T>): Line<T> = FieldLine(field)
fun <T> line(get: Get<T>): Line<T> = GetLine(get)
fun <T> line(switch: Switch<T>): Line<T> = SwitchLine(switch)
fun <T> line(function: Function<T>): Line<T> = FunctionLine(function)
fun <T> line(invoke: Invoke<T>): Line<T> = InvokeLine(invoke)
fun <T> line(variable: Variable): Line<T> = VariableLine(variable)
fun <T> anyLine(any: T): Line<T> = AnyLine(any)

fun <T> get(lhs: Line<T>, name: String) = Get(lhs, name)
fun <T> function(paramTypeStructure: TypeStructure, body: Line<T>) = Function(paramTypeStructure, body)
fun <T> invoke(function: Line<T>, params: Structure<T>) = Invoke(function, params)
fun <T> switch(lhs: Line<T>, cases: Stack<Case<T>>) = Switch(lhs, cases)
fun variable(typeStructure: TypeStructure) = Variable(typeStructure)

fun <T> Line<T>.get(name: String): Line<T> = line(get(this, name))
fun <T> Line<T>.switch(vararg cases: Case<T>): Line<T> = line(switch(this, stack(*cases)))
fun <T> Line<T>.invoke(structure: Structure<T>): Line<T> = line(invoke(this, structure))
fun <T> Line<T>.invoke(vararg params: Line<T>): Line<T> = line(invoke(this, Structure(stack(*params))))

fun <T> Structure<T>.plus(line: Line<T>) = Structure(lineStack.push(line))

fun <T> Switch<T>.line(name: String): Line<T> = lineOrNull(name).notNullOrError("$this.expression($name)")
fun <T> Switch<T>.lineOrNull(name: String): Line<T>? = cases.mapFirst { lineOrNull(name) }
fun <T> Case<T>.lineOrNull(name: String): Line<T>? = notNullIf(this.name == name) { line }

fun <T> Structure<T>.get(name: String): Structure<T> =
	structure(lineStack.onlyOrNull!!.get(name))
