package leo.term.compiler

import leo.Literal
import leo.Script
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm
import leo.term.typed.typedLine
import leo.term.typed.typedTerm

data class Context<out V>(
	val scope: Scope,
	val literalFn: (Literal) -> TypedLine<V>)

fun <V> Context<V>.compileTypedTerm(script: Script): TypedTerm<V> =
	Compiled(this, typedTerm()).plus(script).typedTerm

fun <V> Context<V>.plus(binding: Binding): Context<V> =
	copy(scope = scope.plus(binding))

fun context(): Context<Any?> =
	Context(scope()) { typedLine(it) }