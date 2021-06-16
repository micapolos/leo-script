package leo.named.evaluator

data class Recursive(
	val binding: Binding,
	val baseDictionary: Dictionary,
	val recursiveDictionary: Dictionary)

fun recursive(binding: Binding, baseDictionary: Dictionary, recursiveDictionary: Dictionary) = Recursive(binding, baseDictionary, recursiveDictionary)

fun Recursive.plus(recursive: Recursive) = copy(recursiveDictionary = recursiveDictionary.plus(recursive.recursiveDictionary))