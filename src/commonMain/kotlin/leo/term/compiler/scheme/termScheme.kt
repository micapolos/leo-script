package leo.term.compiler.scheme

import leo.term.AbstractionTerm
import leo.term.ApplicationTerm
import leo.term.NativeTerm
import leo.term.Term
import leo.term.TermAbstraction
import leo.term.TermApplication
import leo.term.TermVariable
import leo.term.VariableTerm
import scheme.Scheme
import scheme.scheme

data class Scope(val depth: Int)
val Scope.push: Scope get() = Scope(depth.inc())

val Term<Scheme>.scheme: Scheme get() =
	Scope(0).scheme(this)

fun Scope.scheme(term: Term<Scheme>): Scheme =
	when (term) {
		is AbstractionTerm -> scheme(term.abstraction)
		is ApplicationTerm -> scheme(term.application)
		is NativeTerm -> term.value
		is VariableTerm -> scheme(term.variable)
	}

fun Scope.scheme(abstraction: TermAbstraction<Scheme>): Scheme =
	("(lambda (v" + depth + ") " + push.scheme(abstraction.term).string + ")").scheme

fun Scope.scheme(application: TermApplication<Scheme>): Scheme =
	("(" + scheme(application.lhs).string + " " + scheme(application.rhs).string + ")").scheme

fun Scope.scheme(variable: TermVariable): Scheme =
	"v${depth - variable.index - 1}".scheme
