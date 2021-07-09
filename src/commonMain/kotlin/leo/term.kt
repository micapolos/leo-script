package leo

sealed class Term<out T>
data class NativeTerm<T>(val value: T) : Term<T>()
data class AbstractionTerm<T>(val abstraction: TermAbstraction<T>) : Term<T>()
data class ApplicationTerm<T>(val application: TermApplication<T>) : Term<T>()
data class VariableTerm<T>(val variable: IndexVariable) : Term<T>()

data class IndexVariable(val index: Int)
data class TermAbstraction<out T>(val variableCount: Int, val term: Term<T>)
data class TermApplication<out T>(val lhs: Term<T>, val rhsStack: Stack<Term<T>>)

fun <T> term(value: T): Term<T> = NativeTerm(value)
fun <T> term(abstraction: TermAbstraction<T>): Term<T> = AbstractionTerm(abstraction)
fun <T> term(application: TermApplication<T>): Term<T> = ApplicationTerm(application)
fun <T> term(variable: IndexVariable): Term<T> = VariableTerm(variable)

fun variable(index: Int) = IndexVariable(index)

// DSL
fun <T> varTerm(index: Int): Term<T> = term(variable(index))
fun <T> lambda(variableCount: Int, term: Term<T>): Term<T> = term(TermAbstraction(variableCount, term))
fun <T> Term<T>.invoke(vararg terms: Term<T>): Term<T> = term(TermApplication(this, stack(*terms)))
