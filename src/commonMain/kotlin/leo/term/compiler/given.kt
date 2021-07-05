package leo.term.compiler

import leo.Type
import leo.term.TermVariable
import leo.term.term
import leo.term.typed.TypedTerm
import leo.term.typed.getOrNull
import leo.term.typed.typed

data class Given(val type: Type)

fun given(type: Type) = Given(type)

fun <V> Given.getOrNull(variable: TermVariable, name: String): TypedTerm<V>? =
	typed(term<V>(variable), type).getOrNull(name)
