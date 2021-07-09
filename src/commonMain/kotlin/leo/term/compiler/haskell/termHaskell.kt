package leo.term.compiler.haskell

import leo.term.AbstractionTerm
import leo.term.ApplicationTerm
import leo.term.IndexVariable
import leo.term.NativeTerm
import leo.term.Term
import leo.term.TermAbstraction
import leo.term.TermApplication
import leo.term.VariableTerm

data class Scope(val depth: Int)

val Scope.push: Scope get() = Scope(depth.inc())

val Term<Haskell>.haskell: Haskell
  get() =
    Scope(0).haskell(this)

fun Scope.haskell(term: Term<Haskell>): Haskell =
  when (term) {
    is AbstractionTerm -> haskell(term.abstraction)
    is ApplicationTerm -> haskell(term.application)
    is NativeTerm -> term.native
    is VariableTerm -> haskell(term.variable)
  }

fun Scope.haskell(abstraction: TermAbstraction<Haskell>): Haskell =
  ("(\\v" + depth + "->v" + depth + " `seq` " + push.haskell(abstraction.term).string + ")").haskell

fun Scope.haskell(application: TermApplication<Haskell>): Haskell =
  ("(" + haskell(application.lhs).string + " " + haskell(application.rhs).string + ")").haskell

fun Scope.haskell(variable: IndexVariable): Haskell =
  "v${depth - variable.index - 1}".haskell
