package leo.named.evaluator

data class Recursive(val dictionary: Dictionary, val binding: Binding)

fun recursive(dictionary: Dictionary, binding: Binding) = Recursive(dictionary, binding)