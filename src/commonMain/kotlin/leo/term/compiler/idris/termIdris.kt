package leo.term.compiler.idris

import leo.term.AbstractionTerm
import leo.term.ApplicationTerm
import leo.term.EmptyTerm
import leo.term.IndexVariable
import leo.term.NativeTerm
import leo.term.Term
import leo.term.TermAbstraction
import leo.term.TermApplication
import leo.term.VariableTerm

data class Scope(val depth: Int)

val Scope.push: Scope get() = Scope(depth.inc())

val Term<Idris>.idris: Idris
  get() =
    Scope(0).idris(this)

fun Scope.idris(term: Term<Idris>): Idris =
  when (term) {
    is EmptyTerm -> "()".idris
    is AbstractionTerm -> idris(term.abstraction)
    is ApplicationTerm -> idris(term.application)
    is NativeTerm -> term.native
    is VariableTerm -> idris(term.variable)
  }

fun Scope.idris(abstraction: TermAbstraction<Idris>): Idris =
  ("(\\v" + depth + "=>" + push.idris(abstraction.term).string + ")").idris

fun Scope.idris(application: TermApplication<Idris>): Idris =
  ("(" + idris(application.lhs).string + " " + idris(application.rhs).string + ")").idris

fun Scope.idris(variable: IndexVariable): Idris =
  "v${depth - variable.index - 1}".idris
