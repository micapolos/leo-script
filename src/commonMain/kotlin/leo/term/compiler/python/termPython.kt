package leo.term.compiler.python

import leo.term.AbstractionTerm
import leo.term.ApplicationTerm
import leo.term.NativeTerm
import leo.term.Term
import leo.term.TermAbstraction
import leo.term.TermApplication
import leo.term.TermVariable
import leo.term.VariableTerm

data class Scope(val depth: Int)

val Scope.push: Scope get() = Scope(depth.inc())

val Term<Python>.python: Python
  get() =
    Scope(0).python(this)

fun Scope.python(term: Term<Python>): Python =
  when (term) {
    is AbstractionTerm -> python(term.abstraction)
    is ApplicationTerm -> python(term.application)
    is NativeTerm -> term.native
    is VariableTerm -> python(term.variable)
  }

fun Scope.python(abstraction: TermAbstraction<Python>): Python =
  ("(lambda v" + depth + ": " + push.python(abstraction.term).string + ")").python

fun Scope.python(application: TermApplication<Python>): Python =
  (python(application.lhs).string + "(" + python(application.rhs).string + ")").python

fun Scope.python(variable: TermVariable): Python =
  "v${depth - variable.index - 1}".python
