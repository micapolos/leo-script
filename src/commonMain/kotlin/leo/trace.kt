package leo

data class Trace(val valueStack: Stack<Value>)

val emptyTrace get() = Trace(stack())
fun Trace.push(value: Value) = Trace(valueStack.push(value))

val Trace.value
	get() =
		value(*valueStack.reverse.map { causeName fieldTo this }.array)
