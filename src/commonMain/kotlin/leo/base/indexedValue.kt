package leo.base

infix fun <V> Int.indexed(value: V) = IndexedValue(this, value)