package leo.term.compiler.julia

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

val Term<Julia>.julia: Julia
  get() =
    Scope(0).julia(this)

fun Scope.julia(term: Term<Julia>): Julia =
  when (term) {
    is EmptyTerm -> "()".julia
    is AbstractionTerm -> julia(term.abstraction)
    is ApplicationTerm -> julia(term.application)
    is NativeTerm -> term.native
    is VariableTerm -> julia(term.variable)
  }

fun Scope.julia(abstraction: TermAbstraction<Julia>): Julia =
  ("(v" + depth + "->" + push.julia(abstraction.term).string + ")").julia

fun Scope.julia(application: TermApplication<Julia>): Julia =
  (julia(application.lhs).string + "(" + julia(application.rhs).string + ")").julia

fun Scope.julia(variable: IndexVariable): Julia =
  "v${depth - variable.index - 1}".julia
