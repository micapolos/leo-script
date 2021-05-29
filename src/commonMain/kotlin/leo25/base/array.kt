package leo25.base

inline fun <R, V> R.fold(array: Array<V>, fn: R.(V) -> R): R =
	array.fold(this) { folded, value ->
		folded.fn(value)
	}

inline fun <R, V> R.foldRight(array: Array<V>, fn: V.(R) -> R): R =
	array.foldRight(this) { folded, value ->
		folded.fn(value)
	}

inline fun <R, V> R.fold(first: V, array: Array<out V>, fn: R.(V) -> R): R =
	fn(first).fold(array, fn)
