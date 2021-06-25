package leo

import leo.base.orIfNull

val Value.optionOrNull: Value? get() =
	resolvePrefixOrNull(optionName) { it }

val Value.presentOrNull: Value? get() =
	resolvePrefixOrNull(presentName) { it }

val Value.absentOrNull: Value? get() =
	resolvePrefixOrNull(absentName) { it }

val Value.presentOption: Value get() =
	make(presentName).make(optionName)

val absentOptionValue: Value get() =
	value().make(absentName).make(optionName)

val Value.liftToOption: Value get() =
	resolvePrefixOrNull(optionName) { this }.orIfNull { presentOption }

fun Value.optionBind(fn: (Value) -> Value): Value =
	optionOrNull
		?.let { optionValue ->
			null
				?: optionValue.presentOrNull?.let { presentValue -> fn(presentValue).liftToOption }
				?: optionValue.absentOrNull?.let { absentOptionValue }
		}
		?: fn(this)

fun Value.optionBindEvaluation(fn: (Value) -> Evaluation<Value>): Evaluation<Value> =
	optionOrNull
		?.let { optionValue ->
			null
				?: optionValue.presentOrNull?.let { presentValue -> fn(presentValue).map { it.liftToOption } }
				?: optionValue.absentOrNull?.let { absentOptionValue.evaluation }
		}
		?: fn(this)
