package leo25

sealed class Binding
data class ValueBinding(val value: Value) : Binding()
data class FunctionBinding(val function: Function) : Binding()

fun binding(value: Value): Binding = ValueBinding(value)
fun binding(function: Function): Binding = FunctionBinding(function)

fun Binding.applyLeo(given: Value): Leo<Value> =
	when (this) {
		is FunctionBinding -> function.applyLeo(given)
		is ValueBinding -> value.leo
	}

val Binding.valueOrNull: Value?
	get() =
		(this as? ValueBinding)?.value
