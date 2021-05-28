package leo25

import leo13.*

data class Trace(val valueStack: Stack<Value>)

val emptyTrace get() = Trace(stack())
fun Trace.push(value: Value) = Trace(valueStack.push(value))

val Trace.value
	get() =
		value(
			traceName fieldTo value(
				*valueStack.map { resolveName fieldTo this }.array
			)
		)