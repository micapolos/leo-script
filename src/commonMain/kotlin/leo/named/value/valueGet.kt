package leo.named.value

import leo.base.orNullIf
import leo.mapFirst
import leo.onlyOrNull

fun <T> Value<T>.line(name: String): ValueLine<T> =
	lineStack.mapFirst { orNullIf(this.name != name) }!!

val <T> Value<T>.line: ValueLine<T> get() =
	lineStack.onlyOrNull!!

val <T> ValueLine<T>.field: Field<T> get() =
	(this as FieldValueLine).field

fun <T> ValueLine<T>.get(name: String): ValueLine<T> =
	field.value.line(name)

fun <T> Value<T>.get(name: String): Value<T> =
	value(line.get(name))
