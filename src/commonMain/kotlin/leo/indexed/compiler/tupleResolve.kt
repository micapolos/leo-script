package leo.indexed.compiler

import leo.indexed.typed.TypedTuple

val <T> TypedTuple<T>.resolve: TypedTuple<T> get() =
	null
		?: resolveGetOrNull
		?: this

val <T> TypedTuple<T>.resolveGetOrNull: TypedTuple<T>? get() =
	null
