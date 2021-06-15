package leo.named.evaluator

data class Recursive(val dictionary: Dictionary)

fun recursive(dictionary: Dictionary) = Recursive(dictionary)

fun Recursive.plus(recursive: Recursive) = copy(dictionary = dictionary.plus(recursive.dictionary))