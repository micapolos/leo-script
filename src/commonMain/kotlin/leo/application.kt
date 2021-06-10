package leo

import leo.base.notNullIf

data class Application(val dictionary: Dictionary, val binding: Binding)

fun application(dictionary: Dictionary, binding: Binding) = Application(dictionary, binding)

fun Application.applyEvaluation(given: Value): Evaluation<Value> =
	dictionary.applyEvaluation(binding, given)

fun Dictionary.applicationOrNull(value: Value): Application? =
	definitionStack.linkOrNull?.let { link ->
		null
			?: Dictionary(link.tail).applicationOrNull(link.head, value)
			?: Dictionary(link.tail).applicationOrNull(value)
	}

fun Dictionary.applicationOrNull(definition: Definition, value: Value): Application? =
	when (definition) {
		is LetDefinition -> applicationOrNull(definition.let, value)
		is RecursiveDefinition -> applicationOrNull(definition.recursive, value)
	}

fun Dictionary.applicationOrNull(let: DefinitionLet, value: Value): Application? =
	notNullIf(value.matches(let.value)) {
		Application(this, let.binding)
	}

fun Dictionary.applicationOrNull(recursive: DictionaryRecursive, value: Value): Application? =
	recursive.dictionary
		.applicationOrNull(value)
		?.let { application ->
			application(
				plus(definition(recursive)),
				application.binding)
		}

