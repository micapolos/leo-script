package leo

sealed class Binding
data class ValueBinding(val value: Value) : Binding()
data class BinderBinding(val binder: Binder) : Binding()
data class RecurseBinding(val recurse: BodyRecurse) : Binding()

fun binding(value: Value): Binding = ValueBinding(value)
fun binding(binder: Binder): Binding = BinderBinding(binder)
fun binding(recurse: BodyRecurse): Binding = RecurseBinding(recurse)

fun Dictionary.applyEvaluation(binding: Binding, given: Value): Evaluation<Value> =
  when (binding) {
    is BinderBinding -> applyEvaluation(given, binding.binder)
    is ValueBinding -> binding.value.evaluation
    is RecurseBinding -> applyEvaluation(given.structureOrThrow.value, binding.recurse.body)
  }

val Binding.valueOrNull: Value?
  get() =
    (this as? ValueBinding)?.value
