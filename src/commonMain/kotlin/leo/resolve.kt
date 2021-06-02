package leo

import leo.base.fold
import leo.base.reverse

fun Dictionary.resolveEvaluation(value: Value): Evaluation<Value> =
	applyOrNullEvaluation(value)
		.or { value.resolveEvaluation }
		.tracing(value)

fun Dictionary.applyOrNullEvaluation(value: Value): Evaluation<Value?> =
	bindingOrNull(value)?.applyEvaluation(value) ?: evaluation(null)

fun Dictionary.set(value: Value): Dictionary =
	plus(definition(script(contentName).type, binding(value))).fold(value.fieldSeq.reverse) { set(it) }

fun Dictionary.set(line: Field): Dictionary =
	plus(
		definition(
			script(line.name).type,
			binding(value(line))
		)
	)