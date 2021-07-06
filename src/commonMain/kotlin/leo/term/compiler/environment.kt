package leo.term.compiler

import leo.Literal
import leo.Script
import leo.term.Term
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm
import leo.term.typed.typed
import leo.typeLine

data class Environment<V>(
	val literalFn: (Literal) -> Term<V>,
	val resolveOrNullFn: (TypedTerm<V>) -> TypedTerm<V>?)

fun <V> Environment<V>.typedTerm(script: Script): TypedTerm<V> =
	context.typedTerm(script)

fun <V> Environment<V>.typedLine(literal: Literal): TypedLine<V> =
	typed(literalFn(literal), literal.typeLine)