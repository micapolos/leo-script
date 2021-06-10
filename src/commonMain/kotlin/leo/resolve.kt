package leo

import leo.base.fold
import leo.base.reverse

fun Dictionary.resolveEvaluation(value: Value): Evaluation<Value> =
	applyOrNullEvaluation(value)
		.or { value.resolveEvaluation }
		.tracing(value)

fun Dictionary.applyOrNullEvaluation(value: Value): Evaluation<Value?> =
	applicationOrNull(value)?.applyEvaluation(value) ?: evaluation(null)

fun Dictionary.plus(value: Value): Dictionary =
	this
		.plusContent(value)
		.plusFields(value)

fun Dictionary.plusContent(value: Value) =
	plus(definition(value(contentName), binding(value)))

fun Dictionary.plusFields(value: Value) =
	fold(value.fieldSeq.reverse) { plus(it) }

fun Dictionary.plus(field: Field): Dictionary =
	plus(
		definition(
			value(field.name),
			binding(value(field))
		)
	)