package leo

import leo.natives.stackTraceValue

data class ValueError(val value: Value) : Error() {
	override fun toString(): String = value.errorValue.toString()
}

val Value.error get() = ValueError(this)

val Throwable.valueOrNull: Value?
	get() =
		(this as? ValueError)?.value

val Throwable.value: Value
	get() =
		valueOrNull ?: javaValue

fun Throwable.causeStackTrace(value: Value): Value =
	value(errorName fieldTo value.plus(causeValue))

val Throwable.javaValue: Value
	get() =
		value(field(literal(toString()))).plus(stackTraceValue)

val Throwable.causeValue: Value
	get() =
		value(causeName fieldTo (valueOrNull ?: javaValue))

val Value.errorValue: Value
	get() =
		value(errorName fieldTo this)

fun <T> Value.throwError(): T =
	throw error

fun <T> T?.notNullOrThrow(fn: () -> Value): T =
	this ?: fn().throwError()

fun <T> T?.throwScriptIfNull(fn: () -> Script): T =
	this ?: error(fn().toString())