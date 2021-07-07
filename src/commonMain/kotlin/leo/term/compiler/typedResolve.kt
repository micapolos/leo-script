package leo.term.compiler

import leo.term.typed.TypedTerm
import leo.term.typed.getOrNull
import leo.term.typed.prefix

val <V> TypedTerm<V>.resolved: TypedTerm<V> get() =
	resolvedOrNull ?: this

val <V> TypedTerm<V>.resolvedOrNull: TypedTerm<V>? get() =
	null
		?: getOrNull

val <V> TypedTerm<V>.getOrNull: TypedTerm<V>? get() =
	prefix { name, rhs ->
		rhs.getOrNull(name)
	}
