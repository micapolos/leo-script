package leo.term.compiler

import leo.Type
import leo.base.notNullIf
import leo.getName
import leo.givenName
import leo.lineTo
import leo.term.TermVariable
import leo.term.term
import leo.term.typed.TypedTerm
import leo.term.typed.typed
import leo.type

data class Given(val type: Type)

fun given(type: Type) = Given(type)

fun <V> Given.resolveOrNull(variable: TermVariable, type: Type): TypedTerm<V>? =
	notNullIf(type == type(getName lineTo type(givenName))) {
		typed(term(variable), this.type)
	}