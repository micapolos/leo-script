package leo

sealed class Term
data class LiteralTerm(val literal: Literal): Term()
data class AbstractionTerm(val abstraction: TermAbstraction): Term()
data class ApplicationTerm(val application: TermApplication): Term()
data class VariableTerm(val variable: TermVariable): Term()

data class TermVariable(val index: Int)
data class TermAbstraction(val term: Term)
data class TermApplication(val lhs: Term, val rhs: Term)

fun term(literal: Literal): Term = LiteralTerm(literal)
fun term(abstraction: TermAbstraction): Term = AbstractionTerm(abstraction)
fun term(application: TermApplication): Term = ApplicationTerm(application)
fun term(variable: TermVariable): Term = VariableTerm(variable)

fun variable(index: Int) = TermVariable(index)

fun v(index: Int): Term = term(variable(index))
fun lambda(term: Term): Term = term(TermAbstraction(term))
operator fun Term.invoke(term: Term): Term = term(TermApplication(this, term))
fun term(string: String): Term = term(literal(string))
fun term(int: Int): Term = term(literal(int))
fun term(double: Double): Term = term(literal(double))
