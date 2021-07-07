package leo.term.compiler

import leo.applyName
import leo.term.typed.TypedTerm
import leo.term.typed.getOrNull
import leo.term.typed.infix
import leo.term.typed.invoke
import leo.term.typed.prefix

val <V> TypedTerm<V>.resolvedOrNull: TypedTerm<V>? get() =
	null
		?: applyOrNull
		?: getOrNull

val <V> TypedTerm<V>.getOrNull: TypedTerm<V>? get() =
	prefix { name, rhs ->
		rhs.getOrNull(name)
	}

val <V> TypedTerm<V>.applyOrNull: TypedTerm<V>? get() =
	infix(applyName) { lhs, rhs ->
		rhs.invoke(lhs)
	}
