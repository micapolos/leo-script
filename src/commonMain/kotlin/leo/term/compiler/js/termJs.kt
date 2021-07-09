package leo.term.compiler.js

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

val Term<Js>.js: Js
  get() =
    Scope(0).js(this)

fun Scope.js(term: Term<Js>): Js =
  when (term) {
    is AbstractionTerm -> js(term.abstraction)
    is ApplicationTerm -> js(term.application)
    is NativeTerm -> term.native
    is VariableTerm -> js(term.variable)
  }

fun Scope.js(abstraction: TermAbstraction<Js>): Js =
  ("(v" + depth + "=>" + push.js(abstraction.term).string + ")").js

fun Scope.js(application: TermApplication<Js>): Js =
  (js(application.lhs).string + "(" + js(application.rhs).string + ")").js

fun Scope.js(variable: IndexVariable): Js =
  "v${depth - variable.index - 1}".js
