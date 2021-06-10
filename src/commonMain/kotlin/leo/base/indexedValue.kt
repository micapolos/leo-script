package leo.base

infix fun <V> Int.indexed(value: V) = IndexedValue(this, value)

fun <V, R> IndexedValue<V>.map(fn: (V) -> R): IndexedValue<R> =
	index indexed fn(value)