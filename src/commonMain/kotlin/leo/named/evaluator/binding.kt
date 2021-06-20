package leo.named.evaluator

import leo.named.expression.BeLetRhs
import leo.named.expression.DoLetRhs
import leo.named.expression.LetRhs
import leo.named.value.Value
import leo.named.value.ValueFunction
import leo.named.value.function
import leo.named.value.line
import leo.named.value.value

sealed class Binding
data class ValueBinding(val value: Value): Binding()
data class FunctionBinding(val function: ValueFunction): Binding()
data class RecursiveBinding(val recursive: Recursive): Binding()

fun binding(value: Value): Binding = ValueBinding(value)
fun binding(function: ValueFunction): Binding = FunctionBinding(function)
fun binding(recursive: Recursive): Binding = RecursiveBinding(recursive)

fun Binding.setRecursive(dictionary: Dictionary): Binding =
	when (this) {
		is FunctionBinding -> binding(function(dictionary, function.body))
		is RecursiveBinding -> TODO()
		is ValueBinding -> this
	}

fun Binding.plus(definition: Definition): Binding =
	when (this) {
		is FunctionBinding -> binding(function(function.dictionary.plus(definition), function.body))
		is RecursiveBinding -> TODO()
		is ValueBinding -> this
	}

val Binding.value: Value get() =
	when (this) {
		is FunctionBinding -> value(line(function))
		is RecursiveBinding -> error("$this.apply($value)")
		is ValueBinding -> value
	}

val LetRhs.binding: Binding get() =
	when (this) {
		is BeLetRhs -> TODO()
		is DoLetRhs -> TODO()
	}