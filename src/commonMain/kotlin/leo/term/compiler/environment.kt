package leo.term.compiler

import leo.Literal
import leo.Script
import leo.term.Term
import leo.term.typed.TypedTerm

data class Environment<V>(
	val literalFn: (Literal) -> Term<V>,
	val resolveOrNullFn: (TypedTerm<V>) -> TypedTerm<V>?)

fun <V> Environment<V>.typedTerm(script: Script): TypedTerm<V> =
	context.typedTerm(script)
