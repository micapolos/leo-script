package leo.term.compiler

import leo.performName
import leo.term.typed.TypedTerm
import leo.term.typed.getOrNull
import leo.term.typed.infix
import leo.term.typed.invoke
import leo.term.typed.prefix

val <V> TypedTerm<V>.resolvedOrNull: TypedTerm<V>? get() =
	null
		?: performOrNull
		?: getOrNull

val <V> TypedTerm<V>.getOrNull: TypedTerm<V>? get() =
	prefix { name, rhs ->
		rhs.getOrNull(name)
	}

val <V> TypedTerm<V>.performOrNull: TypedTerm<V>? get() =
	infix(performName) { lhs, rhs ->
		rhs.invoke(lhs)
	}
