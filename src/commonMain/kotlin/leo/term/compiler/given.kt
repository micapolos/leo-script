package leo.term.compiler

import leo.Type
import leo.nameOrNull
import leo.term.TermVariable
import leo.term.term
import leo.term.typed.TypedTerm
import leo.term.typed.getOrNull
import leo.term.typed.lineTo
import leo.term.typed.typed
import leo.term.typed.typedTerm

data class Given(val type: Type)

fun given(type: Type) = Given(type)

fun <V> Given.resolveOrNull(variable: TermVariable, typedTerm: TypedTerm<V>): TypedTerm<V>? =
	typedTerm.t.nameOrNull?.let { getOrNull(variable, it) }

fun <V> Given.getOrNull(variable: TermVariable, name: String): TypedTerm<V>? =
	typedTerm("given" lineTo typed(term<V>(variable), type)).getOrNull(name)
