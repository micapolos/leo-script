package leo.named.value

import leo.base.orNullIf
import leo.mapFirst
import leo.onlyOrNull

fun <T> Structure<T>.value(name: String): Value<T> =
	valueStack.mapFirst { orNullIf(this.name != name) }!!

val <T> Structure<T>.value: Value<T> get() =
	valueStack.onlyOrNull!!

val <T> Value<T>.field: Field<T> get() =
	(this as FieldValue).field

fun <T> Value<T>.get(name: String): Value<T> =
	field.structure.value(name)

fun <T> Structure<T>.get(name: String): Structure<T> =
	structure(value.get(name))
