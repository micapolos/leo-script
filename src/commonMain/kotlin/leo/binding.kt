package leo

sealed class Binding
data class ValueBinding(val value: Value) : Binding()
data class FunctionBinding(val function: Function) : Binding()
data class RecurseBinding(val function: Function): Binding()

fun binding(value: Value): Binding = ValueBinding(value)
fun binding(function: Function): Binding = FunctionBinding(function)
fun recurseBinding(function: Function): Binding = RecurseBinding(function)

fun Binding.applyEvaluation(given: Value): Evaluation<Value> =
	when (this) {
		is FunctionBinding -> function.applyEvaluation(given)
		is ValueBinding -> value.evaluation
		is RecurseBinding -> function.applyEvaluation(given.structureOrThrow.value)
	}

val Binding.valueOrNull: Value?
	get() =
		(this as? ValueBinding)?.value
