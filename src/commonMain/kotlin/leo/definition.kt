package leo

import leo.base.notNullIf

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

data class DefinitionApplication(val binding: Binding, val recursiveStack: Stack<DefinitionLet>)

fun let(value: Value, binding: Binding) = DefinitionLet(value, binding)
fun recursive(dictionary: Dictionary, let: DefinitionLet) = LetRecursive(dictionary, let)

fun Definition.applicationOrNull(value: Value, recursiveStack: Stack<DefinitionLet>): DefinitionApplication? =
	when (this) {
		is LetDefinition -> let.applicationOrNull(value, recursiveStack)
		is RecursiveDefinition -> recursive.applicationOrNull(value, recursiveStack)
	}

fun DefinitionLet.applicationOrNull(value: Value, recursiveStack: Stack<DefinitionLet>): DefinitionApplication? =
	notNullIf(value.matches(this.value)) {
		DefinitionApplication(binding, recursiveStack)
	}

fun LetRecursive.applicationOrNull(value: Value, recursiveStack: Stack<DefinitionLet>): DefinitionApplication? =
	recursiveStack.push(let).let { recursiveStack ->
		null
			?: let.applicationOrNull(value, recursiveStack)
			?: dictionary.applicationOrNull(value, recursiveStack)
	}

fun DefinitionApplication.applyEvaluation(given: Value): Evaluation<Value> =
	when (binding) {
		is FunctionBinding -> binding.function.fold(recursiveStack.reverse) { push(it) }.applyEvaluation(given)
		is ValueBinding -> binding.value.evaluation
		is RecurseBinding -> binding.recurse.function.fold(recursiveStack.reverse) { push(it) }.applyEvaluation(given.structureOrThrow.value)
	}

val Definition.letOrNull: DefinitionLet? get() = (this as? LetDefinition)?.let