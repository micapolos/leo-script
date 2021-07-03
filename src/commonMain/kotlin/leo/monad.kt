package leo

data class Monad(
	val returnFn: (Value) -> Evaluation<Value>,
	val bindFn: (Value, (Value) -> Evaluation<Value>) -> Evaluation<Value>)

data class MonadDefinition(val pattern: Value, val monad: Monad)

data class MonadDictionary(val definitionStack: Stack<MonadDefinition>)

fun definition(pattern: Value, monad: Monad) =
	MonadDefinition(pattern, monad)

fun dictionary(definition: MonadDefinition, vararg definitions: MonadDefinition) =
	MonadDictionary(stack(stackLink(definition, *definitions)))

fun monadDictionary(vararg definitions: MonadDefinition) =
	MonadDictionary(stack(*definitions))

fun MonadDictionary.plus(definition: MonadDefinition): MonadDictionary =
	definitionStack.push(definition).let(::MonadDictionary)

val identityMonad get() =
	Monad(
		{ value -> value.evaluation },
		{ value, fn -> fn(value) })

val optionMonad get() =
	Monad(
		{ value -> value.liftToOption.evaluation },
		{ value, fn -> value.optionBindEvaluation(fn) })

// TODO: Implement "one of" pattern.
val optionMonadDefinition =
	definition(
		value(optionName fieldTo value(
			presentName fieldTo anythingValue,
			orName fieldTo value(absentName))),
		optionMonad)

val preludeMonadDictionary = dictionary(optionMonadDefinition)