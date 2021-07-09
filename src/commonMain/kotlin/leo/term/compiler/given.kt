package leo.term.compiler

import leo.Type
import leo.nameOrNull
import leo.term.IndexVariable
import leo.term.term
import leo.term.typed.TypedTerm
import leo.term.typed.getOrNull
import leo.term.typed.typed

data class Given(val type: Type)

fun given(type: Type) = Given(type)

fun <V> Given.resolveOrNull(variable: IndexVariable, typedTerm: TypedTerm<V>): TypedTerm<V>? =
  typedTerm.t.nameOrNull?.let { getOrNull(variable, it) }

fun <V> Given.getOrNull(variable: IndexVariable, name: String): TypedTerm<V>? =
  typed(term<V>(variable), type).getOrNull(name)
