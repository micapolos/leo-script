package leo

sealed class Definition
data class LetDefinition(val let: DefinitionLet): Definition()
data class RecursiveDefinition(val recursive: DictionaryRecursive): Definition()

fun definition(let: DefinitionLet): Definition = LetDefinition(let)
fun definition(recursive: DictionaryRecursive): Definition = RecursiveDefinition(recursive)

fun definition(value: Value, binding: Binding): Definition =
	LetDefinition(DefinitionLet(value, binding))

val Field.definition: Definition get() =
	definition(value(name), binding(value(this)))

data class DefinitionLet(val value: Value, val binding: Binding)
data class DictionaryRecursive(val dictionary: Dictionary)

fun let(value: Value, binding: Binding) = DefinitionLet(value, binding)
fun recursive(dictionary: Dictionary) = DictionaryRecursive(dictionary)

val Definition.letOrNull: DefinitionLet? get() = (this as? LetDefinition)?.let
