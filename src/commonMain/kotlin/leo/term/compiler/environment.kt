package leo.term.compiler

import leo.Script
import leo.ScriptLine
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm

data class Environment<V>(
	val resolveMacroOrNullFn: (ScriptLine) -> TypedLine<V>?,
	val resolveOrNullFn: (TypedTerm<V>) -> TypedTerm<V>?)

fun <V> Environment<V>.typedTerm(script: Script): TypedTerm<V> =
	context.typedTerm(script)
