package leo

import leo.base.fold
import leo.base.reverse

fun Dictionary.resolveEvaluation(value: Value): Evaluation<Value> =
	applyOrNullEvaluation(value)
		.or { value.resolveEvaluation }
		.tracing(value)

fun Dictionary.applyOrNullEvaluation(value: Value): Evaluation<Value?> =
	bindingOrNull(value)?.applyEvaluation(value) ?: evaluation(null)

fun Dictionary.bind(value: Value): Dictionary =
	this
		.bindContent(value)
		.bindFields(value)

fun Dictionary.bindContent(value: Value) =
	plus(definition(value(contentName), binding(value)))

fun Dictionary.bindFields(value: Value) =
	fold(value.fieldSeq.reverse) { bind(it) }

fun Dictionary.bind(field: Field): Dictionary =
	plus(
		definition(
			value(field.name),
			binding(value(field))
		)
	)