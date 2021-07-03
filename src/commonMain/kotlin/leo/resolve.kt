package leo

fun Dictionary.resolveEvaluation(value: Value): Evaluation<Value> =
	applyOrNullEvaluation(value)
		.or { value.resolveEvaluation }
		.tracing(value)

fun Dictionary.applyOrNullEvaluation(value: Value): Evaluation<Value?> =
	applicationOrNull(value)?.applyEvaluation(value) ?: evaluation(null)

fun Dictionary.applyEvaluationOrNull(value: Value): Evaluation<Value>? =
	applicationOrNull(value)?.applyEvaluation(value)

fun Dictionary.plus(given: ValueGiven) =
	plus(definition(given))
