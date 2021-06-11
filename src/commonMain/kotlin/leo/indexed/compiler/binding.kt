package leo.indexed.compiler

import leo.TypeLine
import leo.base.runIf
import leo.indexed.expression
import leo.indexed.invoke
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.expressionTuple
import leo.indexed.typed.typed
import leo.indexed.variable

data class Binding<out T>(val typeLine: TypeLine, val isConstant: Boolean)
fun <T> binding(typeLine: TypeLine, isConstant: Boolean): Binding<T> = Binding(typeLine, isConstant)
fun <T> constantBinding(typeLine: TypeLine) = binding<T>(typeLine, isConstant = true)
fun <T> functionBinding(typeLine: TypeLine) = binding<T>(typeLine, isConstant = false)

fun <T> IndexedValue<Binding<T>>.apply(tuple: TypedTuple<T>): Typed<T> =
	  typed(
		  expression<T>(variable(index)).runIf(!value.isConstant) {
			  expression(invoke(this, tuple.expressionTuple))
		  },
		  value.typeLine)