package leo

import leo.base.notNullIf

data class ValueGiven(val value: Value)

fun given(value: Value) = ValueGiven(value)

fun ValueGiven.getOrNull(value: Value): Value? =
	value.nameOrNull?.let { getOrNull(it) }

fun ValueGiven.getOrNull(name: String): Value? =
	null
		?: value.selectDeeplyOrNull(name)
		?: notNullIf(name == givenName) { value(givenName fieldTo value) }