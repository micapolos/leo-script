package leo

sealed class Binding
data class ValueBinding(val value: Value) : Binding()
data class FunctionBinding(val function: Function) : Binding()

fun binding(value: Value): Binding = ValueBinding(value)
fun binding(function: Function): Binding = FunctionBinding(function)

fun Binding.applyEvaluation(given: Value): Evaluation<Value> =
	when (this) {
		is FunctionBinding -> function.applyEvaluation(given)
		is ValueBinding -> value.evaluation
	}

val Binding.valueOrNull: Value?
	get() =
		(this as? ValueBinding)?.value
