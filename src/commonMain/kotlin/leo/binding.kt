package leo

sealed class Binding
data class ValueBinding(val value: Value) : Binding()
data class BodyBinding(val body: Body) : Binding()
data class RecurseBinding(val recurse: BodyRecurse): Binding()

fun binding(value: Value): Binding = ValueBinding(value)
fun binding(body: Body): Binding = BodyBinding(body)
fun binding(recurse: BodyRecurse): Binding = RecurseBinding(recurse)

fun Dictionary.applyEvaluation(binding: Binding, given: Value): Evaluation<Value> =
	when (binding) {
		is BodyBinding -> applyEvaluation(binding.body, given)
		is ValueBinding -> binding.value.evaluation
		is RecurseBinding -> applyEvaluation(binding.recurse.body, given.structureOrThrow.value)
	}

val Binding.valueOrNull: Value?
	get() =
		(this as? ValueBinding)?.value
