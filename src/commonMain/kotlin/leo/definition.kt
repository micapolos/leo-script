package leo

sealed class Definition
data class LetDefinition(val let: DefinitionLet): Definition()
data class RecursiveDefinition(val recursive: LetRecursive): Definition()

fun definition(let: DefinitionLet): Definition = LetDefinition(let)
fun definition(recursive: LetRecursive): Definition = RecursiveDefinition(recursive)

fun definition(value: Value, binding: Binding): Definition =
	LetDefinition(DefinitionLet(value, binding))

val Field.definition: Definition get() =
	definition(value(name), binding(value(this)))

data class DefinitionLet(val value: Value, val binding: Binding)
data class LetRecursive(val dictionary: Dictionary, val let: DefinitionLet)

data class BindingApplication(val dictionary: Dictionary, val binding: Binding)
fun application(dictionary: Dictionary, binding: Binding) = BindingApplication(dictionary, binding)

fun let(value: Value, binding: Binding) = DefinitionLet(value, binding)
fun recursive(dictionary: Dictionary, let: DefinitionLet) = LetRecursive(dictionary, let)

fun BindingApplication.applyEvaluation(given: Value): Evaluation<Value> =
	dictionary.applyEvaluation(binding, given)

val Definition.letOrNull: DefinitionLet? get() = (this as? LetDefinition)?.let

val Dictionary.recursive: LetRecursive get() =
	definitionStack.linkOrNull
		.notNullOrThrow { value("recursive") }
		.let {
			recursive(
				Dictionary(it.tail),
				it.head.letOrNull.notNullOrThrow { value("recursive") })
		}