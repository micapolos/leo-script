package leo.typed.compiled

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
data class CompiledTuple<out V>(val tuple: Tuple<V>, val typeStructure: TypeStructure)
data class CompiledFunction<out V>(val function: Function<V>, val typeFunction: TypeFunction)
data class CompiledSelect<out V>(val caseOrNull: Case<V>?, val choice: TypeChoice)
data class CompiledChoice<out V>(val expression: Expression<V>, val choice: TypeChoice)
data class CompiledStructure<out V>(val expression: Expression<V>, val typeStructure: TypeStructure)

data class Fragment<out V>(val expression: Expression<V>, val tuple: Tuple<V>)

sealed class Expression<out V>
data class TupleExpression<out V>(val tuple: Tuple<V>): Expression<V>()
data class ApplyExpression<V>(val apply: Apply<V>): Expression<V>()
data class VariableExpression<out V>(val variable: TypeVariable): Expression<V>()
data class SelectExpression<V>(val select: Select<V>): Expression<V>()
data class SwitchExpression<V>(val switch: Switch<V>): Expression<V>()
data class ContentExpression<V>(val content: Content<V>): Expression<V>()
data class BindExpression<V>(val bind: Bind<V>): Expression<V>()
data class LinkExpression<V>(val link: Link<V>): Expression<V>()

data class Tuple<out V>(val lineStack: Stack<Line<V>>)

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
data class Content<out V>(val lhs: Compiled<V>)
data class Binding<out V>(val type: Type, val compiled: Compiled<V>)
data class Bind<out V>(val binding: Binding<V>, val compiled: Compiled<V>)
data class Link<out V>(val lhsCompiled: Compiled<V>, val rhsCompiledLine: CompiledLine<V>)
data class TypeVariable(val type: Type)

sealed class CompiledSelectLine<out V>
data class TheCompiledSelectLine<V>(val the: CompiledLineThe<V>): CompiledSelectLine<V>()
data class NotCompiledSelectLine<V>(val not: TypeLineNot): CompiledSelectLine<V>()

data class CompiledLineThe<out V>(val compiledLine: CompiledLine<V>)
data class TypeLineNot(val typeLine: TypeLine)

fun <V> line(the: CompiledLineThe<V>): CompiledSelectLine<V> = TheCompiledSelectLine(the)
fun <V> line(not: TypeLineNot): CompiledSelectLine<V> = NotCompiledSelectLine(not)

fun <V> the(compiledLine: CompiledLine<V>) = CompiledLineThe(compiledLine)
fun not(typeLine: TypeLine) = TypeLineNot(typeLine)

fun <V> tuple(vararg lines: Line<V>) = Tuple(stack(*lines))
fun <V> expression(tuple: Tuple<V>): Expression<V> = TupleExpression(tuple)
fun <V> expression(apply: Apply<V>): Expression<V> = ApplyExpression(apply)
fun <V> expression(select: Select<V>): Expression<V> = SelectExpression(select)
fun <V> expression(switch: Switch<V>): Expression<V> = SwitchExpression(switch)
fun <V> expression(content: Content<V>): Expression<V> = ContentExpression(content)
fun <V> expression(bind: Bind<V>): Expression<V> = BindExpression(bind)
fun <V> expression(variable: TypeVariable): Expression<V> = VariableExpression(variable)
fun <V> expression(link: Link<V>): Expression<V> = LinkExpression(link)

fun <V> nativeLine(native: V): Line<V> = NativeLine(native)
fun <V> line(field: Field<V>): Line<V> = FieldLine(field)
fun <V> line(function: Function<V>): Line<V> = FunctionLine(function)
fun <V> line(get: Get<V>): Line<V> = GetLine(get)

fun <V> compiled(expression: Expression<V>, type: Type): Compiled<V> = Compiled(expression, type)
fun <V> compiled(tuple: Tuple<V>, structure: TypeStructure) = CompiledTuple(tuple, structure)
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
fun <V> content(lhs: Compiled<V>) = Content(lhs)
fun <V> binding(type: Type, compiled: Compiled<V>) = Binding(type, compiled)
fun <V> bind(binding: Binding<V>, compiled: Compiled<V>) = Bind(binding, compiled)
fun <V> link(lhs: Compiled<V>, rhs: CompiledLine<V>) = Link(lhs, rhs)
fun variable(type: Type) = TypeVariable(type)

infix fun <V> String.lineTo(compiled: Compiled<V>): CompiledLine<V> =
  compiled(line(field(this, compiled)), this lineTo compiled.type)

val <V> Expression<V>.tupleOrNull: Tuple<V>? get() = (this as? TupleExpression<V>)?.tuple
val <V> Line<V>.fieldOrNull: Field<V>? get() = (this as? FieldLine<V>)?.field

fun <V> nativeCompiled(native: V, typeLine: TypeLine): Compiled<V> = compiled(nativeCompiledLine(native, typeLine))
fun <V> nativeCompiled(native: V): Compiled<V> = compiled(nativeCompiledLine(native))

fun <V> nativeCompiledLine(native: V, typeLine: TypeLine): CompiledLine<V> = compiled(nativeLine(native), typeLine)
fun <V> nativeCompiledLine(native: V): CompiledLine<V> = compiled(nativeLine(native), line(atom(primitive(native(script("native"))))))

fun <V> case(name: String, line: Line<V>) = Case(name, line)
fun <V> select(caseOrNull: Case<V>?, typeChoice: TypeChoice) = CompiledSelect(caseOrNull, typeChoice)
fun <V> compiledSelect(): CompiledSelect<V> = select(null, choice())
