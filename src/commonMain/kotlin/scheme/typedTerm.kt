package scheme

import leo.Term
import leo.Typed
import leo.term

val Typed.schemeTerm: Term<Scheme> get() = term { term(it) }
