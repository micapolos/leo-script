package leo.indexed.typed

import leo.Literal
import leo.Stack
import leo.StructureType
import leo.Type
import leo.TypeLine
import leo.TypeStructure
import leo.indexed.Expression
import leo.indexed.Tuple
import leo.indexed.expression
import leo.isEmpty
import leo.lineTo
import leo.map
import leo.onlyOrNull
import leo.push
import leo.stack
import leo.typeLine

data class Typed<out T>(val expression: Expression<T>, val typeLine: TypeLine)
data class TypedTuple<out T>(val typedStack: Stack<Typed<T>>)

fun <T> typed(expression: Expression<T>, typeLine: TypeLine) = Typed(expression, typeLine)
infix fun <T> Expression<T>.of(typeLine: TypeLine) = Typed(this, typeLine)
fun <T> tuple(vararg typeds: Typed<T>) = TypedTuple(stack(*typeds))
infix fun <T> String.typedTo(tuple: TypedTuple<T>) = tuple.expression.of(this lineTo tuple.type)

val TypedTuple<*>.isEmpty get() = typedStack.isEmpty
val <T> TypedTuple<T>.onlyTypedOrNull: Typed<T>? get() = typedStack.onlyOrNull
fun <T> TypedTuple<T>.plus(typed: Typed<T>) = typedStack.push(typed).let(::TypedTuple)

val <T> TypedTuple<T>.expression: Expression<T> get() =
	if (isEmpty) expression(leo.indexed.tuple())
	else onlyTypedOrNull
		?.let { it.expression }
		?:expression(typedStack.map { expression }.let(::Tuple))
val <T> TypedTuple<T>.type: Type get() = typedStack.map { typeLine }.let(::TypeStructure).let(::StructureType)
fun <T> typed(literal: Literal): Typed<T> = expression<T>(literal).of(literal.typeLine)