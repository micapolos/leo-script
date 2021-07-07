package leo.term.typed

import leo.base.ifOrNull
import leo.isEmpty
import leo.name

fun <V, R: Any> TypedTerm<V>.empty(fn: () -> R?): R? =
	ifOrNull(t.isEmpty) { fn() }

fun <V, R: Any> TypedTerm<V>.infix(fn: (TypedTerm<V>, String, TypedTerm<V>) -> R?): R? =
	pairOrNull?.let { (term, line) ->
		line.lineContentOrNull?.let { rhs ->
			fn(term, line.t.name, rhs)
		}
	}

fun <V, R: Any> TypedTerm<V>.prefix(fn: (String, TypedTerm<V>) -> R?): R? =
	infix { lhs, name, rhs ->
		lhs.empty {
			fn(name, rhs)
		}
	}

fun <V, R: Any> TypedTerm<V>.infix(name: String, fn: (TypedTerm<V>, TypedTerm<V>) -> R?): R? =
	infix { lhs, infixName, rhs ->
		ifOrNull(infixName == name) {
			fn(lhs, rhs)
		}
	}

fun <V, R: Any> TypedTerm<V>.prefix(name: String, fn: (TypedTerm<V>) -> R?): R? =
	prefix { prefixName, rhs ->
		ifOrNull(prefixName == name) {
			fn(rhs)
		}
	}
