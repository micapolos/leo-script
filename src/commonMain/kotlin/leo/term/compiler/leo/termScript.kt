package leo.term.compiler.leo

import leo.Script
import leo.lineTo
import leo.literal
import leo.plus
import leo.script
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

val Term<Script>.script: Script
	get() =
		Scope(0).script(this)

fun Scope.script(term: Term<Script>): Script =
	when (term) {
		is AbstractionTerm -> script(term.abstraction)
		is ApplicationTerm -> script(term.application)
		is NativeTerm -> term.native
		is VariableTerm -> script(term.variable)
	}

fun Scope.script(abstraction: TermAbstraction<Script>): Script =
	script("lambda" lineTo push.script(abstraction.term))

fun Scope.script(application: TermApplication<Script>): Script =
	script(application.lhs).plus("apply" lineTo script(application.rhs))

fun Scope.script(variable: TermVariable): Script =
	script("variable" lineTo script(literal(depth - variable.index - 1)))
