package leo.term.compiler

import leo.Script
import leo.term.typed.TypedTerm
import leo.term.typed.typedTerm

data class Context<V>(
	val environment: Environment<V>,
	val scope: Scope)

val <V> Environment<V>.context get() =
	Context(this, scope())

fun <V> Context<V>.typedTerm(script: Script): TypedTerm<V> =
	Compiled(this, typedTerm()).plus(script).typedTerm

fun <V> Context<V>.plus(binding: Binding): Context<V> =
	copy(scope = scope.plus(binding))

fun <V> Context<V>.resolve(typedTerm: TypedTerm<V>): TypedTerm<V> =
	null
		?: scope.resolveOrNull(typedTerm)
		?: environment.resolveOrNullFn(typedTerm)
		?: typedTerm
