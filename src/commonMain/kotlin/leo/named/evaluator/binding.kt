package leo.named.evaluator

import leo.named.value.Value

sealed class Binding
data class ValueBinding(val value: Value): Binding()
data class RecursiveBinding(val recursive: Recursive): Binding()

fun binding(value: Value): Binding = ValueBinding(value)
fun binding(recursive: Recursive): Binding = RecursiveBinding(recursive)
