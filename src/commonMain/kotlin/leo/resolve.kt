package leo

import leo.base.fold
import leo.base.reverse

fun Dictionary.resolveEvaluation(value: Value): Evaluation<Value> =
	applyOrNullEvaluation(value)
		.or { value.resolveEvaluation }
		.tracing(value)

fun Dictionary.applyOrNullEvaluation(value: Value): Evaluation<Value?> =
	applicationOrNull(value)?.applyEvaluation(value) ?: evaluation(null)

fun Dictionary.applyEvaluationOrNull(value: Value): Evaluation<Value>? =
	applicationOrNull(value)?.applyEvaluation(value)

fun Dictionary.plus(value: Value): Dictionary =
	this
		.plusGiven(value)
		.plusFields(value)
		.plus(given(value))

fun Dictionary.plus(given: ValueGiven) =
	plus(definition(given))

fun Dictionary.plusGiven(value: Value) =
	plus(definition(value(givenName), binding(value(givenName fieldTo value))))

fun Dictionary.plusFields(value: Value) =
	fold(value.fieldSeq.reverse) { plus(it) }

fun Dictionary.plus(field: Field): Dictionary =
	plus(
		definition(
			value(field.name),
			binding(value(field))
		)
	)