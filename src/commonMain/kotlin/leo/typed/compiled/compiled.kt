package leo.typed.compiled

import leo.Empty
import leo.IndexVariable
import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.TypeField
import leo.TypeFunction
import leo.TypeLine
import leo.TypeStructure
import leo.anyScriptLine
import leo.atom
import leo.choice
import leo.line
import leo.lineTo
import leo.native
import leo.primitive
import leo.script
import leo.stack

data class Compiled<out V>(val expression: Expression<V>, val type: Type) {
  override fun toString() = toScriptLine { it.anyScriptLine }.toString()
}

data class CompiledLine<out V>(val line: Line<V>, val typeLine: TypeLine)
data class CompiledField<out V>(val field: Field<V>, val typeField: TypeField)
data class CompiledFunction<out V>(val function: Function<V>, val typeFunction: TypeFunction)
data class CompiledSelect<out V>(val caseOrNull: Case<V>?, val choice: TypeChoice)
data class CompiledChoice<out V>(val expression: Expression<V>, val choice: TypeChoice)
data class CompiledStructure<out V>(val expression: Expression<V>, val typeStructure: TypeStructure)

sealed class Expression<out V>
data class EmptyExpression<out V>(val empty: Empty): Expression<V>()
data class LinkExpression<V>(val link: Link<V>): Expression<V>()
data class ApplyExpression<V>(val apply: Apply<V>): Expression<V>()
data class VariableExpression<out V>(val variable: IndexVariable): Expression<V>()
data class SelectExpression<V>(val select: Select<V>): Expression<V>()
data class SwitchExpression<V>(val switch: Switch<V>): Expression<V>()
data class ContentExpression<V>(val content: CompiledContent<V>): Expression<V>()

sealed class Line<out V>
data class NativeLine<V>(val native: V): Line<V>()
data class FieldLine<V>(val field: Field<V>): Line<V>()
data class FunctionLine<V>(val function: Function<V>): Line<V>()
data class GetLine<V>(val get: Get<V>): Line<V>()

data class Case<out V>(val name: String, val line: Line<V>)
data class Field<out V>(val name: String, val rhs: Compiled<V>)
data class Select<out V>(val choice: TypeChoice, val case: Case<V>)
data class Function<out V>(val paramType: Type, val body: Body<V>)
data class Body<out V>(val compiled: Compiled<V>, val isRecursive: Boolean)
data class Get<out V>(val lhs: Compiled<V>, val index: Int)
data class Apply<out V>(val lhs: Compiled<V>, val rhs: Compiled<V>)
data class Switch<out V>(val lhs: Compiled<V>, val caseStack: Stack<Compiled<V>>)
data class CompiledContent<out V>(val lhs: Compiled<V>)
data class Link<out V>(val lhs: Compiled<V>, val rhsLine: CompiledLine<V>)

object Content

sealed class Op<out V>
data class ContentOp<V>(val content: Content): Op<V>()
data class AppendOp<V>(val append: CompiledLineAppend<V>): Op<V>()
data class ApplyOp<V>(val apply: CompiledApply<V>): Op<V>()
data class SwitchOp<V>(val switch: CompiledCases<V>): Op<V>()

data class CompiledLineAppend<out V>(val line: CompiledLine<V>)
data class CompiledApply<out V>(val compiled: Compiled<V>)
data class CompiledCases<out V>(val caseStack: Stack<Compiled<V>>)

sealed class CompiledSelectLine<out V>
data class TheCompiledSelectLine<V>(val the: CompiledLineThe<V>): CompiledSelectLine<V>()
data class NotCompiledSelectLine<V>(val not: TypeLineNot): CompiledSelectLine<V>()

data class CompiledLineThe<out V>(val compiledLine: CompiledLine<V>)
data class TypeLineNot(val typeLine: TypeLine)

fun <V> line(the: CompiledLineThe<V>): CompiledSelectLine<V> = TheCompiledSelectLine(the)
fun <V> line(not: TypeLineNot): CompiledSelectLine<V> = NotCompiledSelectLine(not)

fun <V> the(compiledLine: CompiledLine<V>) = CompiledLineThe(compiledLine)
fun not(typeLine: TypeLine) = TypeLineNot(typeLine)

fun <V> expression(empty: Empty): Expression<V> = EmptyExpression(empty)
fun <V> expression(apply: Apply<V>): Expression<V> = ApplyExpression(apply)
fun <V> expression(select: Select<V>): Expression<V> = SelectExpression(select)
fun <V> expression(switch: Switch<V>): Expression<V> = SwitchExpression(switch)
fun <V> expression(content: CompiledContent<V>): Expression<V> = ContentExpression(content)
fun <V> expression(variable: IndexVariable): Expression<V> = VariableExpression(variable)
fun <V> expression(link: Link<V>): Expression<V> = LinkExpression(link)

fun <V> nativeLine(native: V): Line<V> = NativeLine(native)
fun <V> line(field: Field<V>): Line<V> = FieldLine(field)
fun <V> line(function: Function<V>): Line<V> = FunctionLine(function)
fun <V> line(get: Get<V>): Line<V> = GetLine(get)

fun <V> compiled(expression: Expression<V>, type: Type): Compiled<V> = Compiled(expression, type)
fun <V> compiled(line: Line<V>, typeLine: TypeLine) = CompiledLine(line, typeLine)
fun <V> compiled(field: Field<V>, typeField: TypeField) = CompiledField(field, typeField)
fun <V> compiled(function: Function<V>, typeFunction: TypeFunction) = CompiledFunction(function, typeFunction)
fun <V> compiled(expression: Expression<V>, typeChoice: TypeChoice) = CompiledChoice(expression, typeChoice)
fun <V> compiled(expression: Expression<V>, typeStructure: TypeStructure) = CompiledStructure(expression, typeStructure)

fun <V> function(paramType: Type, body: Body<V>) = Function(paramType, body)
fun <V> body(compiled: Compiled<V>) = Body(compiled, isRecursive = false)
fun <V> recursive(body: Body<V>) = body.copy(isRecursive = true)
fun <V> apply(lhs: Compiled<V>, rhs: Compiled<V>) = Apply(lhs, rhs)
fun <V> field(name: String, rhs: Compiled<V>) = Field(name, rhs)
fun <V> get(lhs: Compiled<V>, index: Int) = Get(lhs, index)
fun <V> select(choice: TypeChoice, case: Case<V>) = Select(choice, case)
fun <V> switch(lhs: Compiled<V>, vararg cases: Compiled<V>) = Switch(lhs, stack(*cases))
fun <V> content(lhs: Compiled<V>) = CompiledContent(lhs)
fun <V> link(lhs: Compiled<V>, rhs: CompiledLine<V>) = Link(lhs, rhs)

fun <V> append(line: CompiledLine<V>) = CompiledLineAppend(line)
fun <V> apply(compiled: Compiled<V>) = CompiledApply(compiled)
fun <V> compiledCases(vararg cases: Compiled<V>) = CompiledCases(stack(*cases))
fun <V> cases(case: Compiled<V>, vararg cases: Compiled<V>) = CompiledCases(stack(case, *cases))

fun <V> op(content: Content): Op<V> = ContentOp(content)
fun <V> op(apply: CompiledApply<V>): Op<V> = ApplyOp(apply)
fun <V> op(switch: CompiledCases<V>): Op<V> = SwitchOp(switch)
fun <V> op(append: CompiledLineAppend<V>): Op<V> = AppendOp(append)

infix fun <V> String.lineTo(compiled: Compiled<V>): CompiledLine<V> =
  compiled(line(field(this, compiled)), this lineTo compiled.type)

val <V> Line<V>.fieldOrNull: Field<V>? get() = (this as? FieldLine<V>)?.field

fun <V> nativeCompiled(native: V, typeLine: TypeLine): Compiled<V> = compiled(nativeCompiledLine(native, typeLine))
fun <V> nativeCompiled(native: V): Compiled<V> = compiled(nativeCompiledLine(native))

fun <V> nativeCompiledLine(native: V, typeLine: TypeLine): CompiledLine<V> = compiled(nativeLine(native), typeLine)
fun <V> nativeCompiledLine(native: V): CompiledLine<V> = compiled(nativeLine(native), line(atom(primitive(native(script("native"))))))

fun <V> case(name: String, line: Line<V>) = Case(name, line)
fun <V> select(caseOrNull: Case<V>?, typeChoice: TypeChoice) = CompiledSelect(caseOrNull, typeChoice)
fun <V> compiledSelect(): CompiledSelect<V> = select(null, choice())
