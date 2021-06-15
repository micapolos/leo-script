package leo.named.evaluator

import leo.named.value.Value
import leo.named.value.ValueFunction

sealed class Binding
data class ValueBinding(val value: Value): Binding()
data class FunctionBinding(val function: ValueFunction): Binding()
data class RecursiveBinding(val recursive: Recursive): Binding()

fun binding(value: Value): Binding = ValueBinding(value)
fun binding(function: ValueFunction): Binding = FunctionBinding(function)
fun binding(recursive: Recursive): Binding = RecursiveBinding(recursive)

fun Binding.bindRecursive(dictionary: Dictionary): Binding =
	when (this) {
		is FunctionBinding -> binding(function.copy(dictionary = function.dictionary.plusRecursive(dictionary)))
		is RecursiveBinding -> binding(recursive(recursive.binding.bindRecursive(dictionary), recursive.dictionary))
		is ValueBinding -> this
	}
