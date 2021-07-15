package leo.term.compiled

import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.TypeFunction
import leo.TypeLine
import leo.TypeStructure
import leo.atom
import leo.choice
import leo.line
import leo.lineTo
import leo.named.value.anyScriptLine
import leo.native
import leo.primitive
import leo.script
import leo.stack
import leo.term.IndexVariable

data class Compiled<out V>(val expression: Expression<V>, val type: Type) {
  override fun toString() = toScriptLine { it.anyScriptLine }.toString()
}

data class CompiledLine<out V>(val line: Line<V>, val typeLine: TypeLine)
data class CompiledTuple<out V>(val tuple: Tuple<V>, val typeStructure: TypeStructure)
data class CompiledFunction<out V>(val function: Function<V>, val typeFunction: TypeFunction)
data class CompiledSelect<out V>(val lineIndexedOrNull: LineIndexed<V>?, val choice: TypeChoice)

data class Fragment<out V>(val expression: Expression<V>, val tuple: Tuple<V>)

sealed class Expression<out V>
data class TupleExpression<out V>(val tuple: Tuple<V>): Expression<V>()
data class ApplyExpression<V>(val apply: Apply<V>): Expression<V>()
data class VariableExpression<out V>(val variable: IndexVariable): Expression<V>()
data class SelectExpression<V>(val select: Select<V>): Expression<V>()
data class SwitchExpression<V>(val switch: Switch<V>): Expression<V>()

data class Tuple<out V>(val lineStack: Stack<Line<V>>)

sealed class Line<out V>
data class NativeLine<V>(val native: V): Line<V>()
data class FieldLine<V>(val field: Field<V>): Line<V>()
data class FunctionLine<V>(val function: Function<V>): Line<V>()
data class GetLine<V>(val get: Get<V>): Line<V>()

data class LineIndexed<out V>(val index: Int, val line: Line<V>)
data class Field<out V>(val name: String, val rhs: Compiled<V>)
data class Select<out V>(val choice: TypeChoice, val lineIndexed: LineIndexed<V>)
data class Function<out V>(val paramType: Type, val body: Body<V>)
data class Body<out V>(val compiled: Compiled<V>, val isRecursive: Boolean)
data class Get<out V>(val lhs: Compiled<V>, val index: Int)
data class Apply<out V>(val lhs: Compiled<V>, val rhs: Compiled<V>)
data class Switch<out V>(val lhs: Compiled<V>, val caseStack: Stack<Compiled<V>>)

fun <V> tuple(vararg lines: Line<V>) = Tuple(stack(*lines))
fun <V> expression(tuple: Tuple<V>): Expression<V> = TupleExpression(tuple)
fun <V> expression(apply: Apply<V>): Expression<V> = ApplyExpression(apply)
fun <V> expression(variable: IndexVariable): Expression<V> = VariableExpression(variable)
fun <V> expression(select: Select<V>): Expression<V> = SelectExpression(select)
fun <V> expression(switch: Switch<V>): Expression<V> = SwitchExpression(switch)

fun <V> nativeLine(native: V): Line<V> = NativeLine(native)
fun <V> line(field: Field<V>): Line<V> = FieldLine(field)
fun <V> line(function: Function<V>): Line<V> = FunctionLine(function)
fun <V> line(get: Get<V>): Line<V> = GetLine(get)

fun <V> compiled(expression: Expression<V>, type: Type): Compiled<V> = Compiled(expression, type)
fun <V> compiled(tuple: Tuple<V>, structure: TypeStructure) = CompiledTuple(tuple, structure)
fun <V> compiled(line: Line<V>, typeLine: TypeLine) = CompiledLine(line, typeLine)

fun <V> function(paramType: Type, body: Body<V>) = Function(paramType, body)
fun <V> body(compiled: Compiled<V>) = Body(compiled, isRecursive = false)
fun <V> recursive(body: Body<V>) = body.copy(isRecursive = true)
fun <V> apply(lhs: Compiled<V>, rhs: Compiled<V>) = Apply(lhs, rhs)
fun <V> field(name: String, rhs: Compiled<V>) = Field(name, rhs)
fun <V> get(lhs: Compiled<V>, index: Int) = Get(lhs, index)
fun <V> select(choice: TypeChoice, lineIndexedOrNull: LineIndexed<V>) = Select(choice, lineIndexedOrNull)
fun <V> switch(lhs: Compiled<V>, vararg cases: Compiled<V>) = Switch(lhs, stack(*cases))

infix fun <V> String.lineTo(compiled: Compiled<V>): CompiledLine<V> =
  compiled(line(field(this, compiled)), this lineTo compiled.type)

val <V> Expression<V>.tupleOrNull: Tuple<V>? get() = (this as? TupleExpression<V>)?.tuple
val <V> Line<V>.fieldOrNull: Field<V>? get() = (this as? FieldLine<V>)?.field

fun <V> nativeCompiled(native: V, typeLine: TypeLine): Compiled<V> = compiled(nativeCompiledLine(native, typeLine))
fun <V> nativeCompiled(native: V): Compiled<V> = compiled(nativeCompiledLine(native))

fun <V> nativeCompiledLine(native: V, typeLine: TypeLine): CompiledLine<V> = compiled(nativeLine(native), typeLine)
fun <V> nativeCompiledLine(native: V): CompiledLine<V> = compiled(nativeLine(native), line(atom(primitive(native(script("native"))))))

fun <V> indexed(index: Int, line: Line<V>) = LineIndexed(index, line)
fun <V> select(lineIndexedOrNull: LineIndexed<V>?, typeChoice: TypeChoice) = CompiledSelect(lineIndexedOrNull, typeChoice)
fun <V> compiledSelect(): CompiledSelect<V> = select(null, choice())