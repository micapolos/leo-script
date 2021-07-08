package leo.term.compiler

import leo.applyName
import leo.contentName
import leo.term.typed.TypedTerm
import leo.term.typed.content
import leo.term.typed.getOrNull
import leo.term.typed.infix
import leo.term.typed.invoke
import leo.term.typed.prefix

val <V> TypedTerm<V>.resolvedOrNull: TypedTerm<V>? get() =
	null
		?: resolveApplyOrNull
		?: resolveContentOrNull
		?: resolveGetOrNull

val <V> TypedTerm<V>.resolveApplyOrNull: TypedTerm<V>? get() =
	infix(applyName) { lhs, rhs -> rhs.invoke(lhs) }

val <V> TypedTerm<V>.resolveContentOrNull: TypedTerm<V>? get() =
	prefix(contentName) { it.content }

val <V> TypedTerm<V>.resolveGetOrNull: TypedTerm<V>? get() =
	prefix { name, rhs -> rhs.getOrNull(name) }

