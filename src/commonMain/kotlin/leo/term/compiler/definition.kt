package leo.term.compiler

import leo.Type
import leo.base.notNullIf
import leo.term.TermVariable
import leo.term.invoke
import leo.term.term
import leo.term.typed.TypedTerm
import leo.term.typed.typed

data class Definition(val type: Type)

fun <V> Definition.resolveOrNull(variable: TermVariable, typedTerm: TypedTerm<V>): TypedTerm<V>? =
	notNullIf(typedTerm.t == type) {
		typed(term<V>(variable).invoke(typedTerm.v), typedTerm.t)
	}