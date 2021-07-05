package leo.term.compiler

import leo.Script
import leo.term.typed.TypedTerm

fun <V> Script.typedTerm(context: Context<V>): TypedTerm<V> =
	context.compileTypedTerm(this)