package leo.term.compiler

import leo.Script
import leo.Type
import leo.term.typed.TypedTerm

data class Context<V>(
	val environment: Environment<V>,
	val scope: Scope)

val <V> Environment<V>.context get() =
	Context(this, scope())

fun <V> Context<V>.typedTerm(script: Script): TypedTerm<V> =
	module.compiler.plus(script).compiledTypedTerm

fun <V> Context<V>.plus(binding: Binding): Context<V> =
	copy(scope = scope.plus(binding))

fun <V> Context<V>.resolve(typedTerm: TypedTerm<V>): TypedTerm<V> =
	null
		?: scope.resolveOrNull(typedTerm)
		?: environment.resolveOrNullFn(typedTerm)
		?: typedTerm.resolvedOrNull
		?: typedTerm

fun <V> Context<V>.type(script: Script): Type =
	typedTerm(script).type