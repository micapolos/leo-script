package leo.named.evaluator

data class Recursive(val binding: Binding, val dictionary: Dictionary)

fun recursive(binding: Binding, dictionary: Dictionary) = Recursive(binding, dictionary)

fun Recursive.plus(recursive: Recursive) = copy(dictionary = dictionary.plus(recursive.dictionary))