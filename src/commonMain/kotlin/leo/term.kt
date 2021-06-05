package leo

sealed class Term<out T>
data class NativeTerm<T>(val value: T): Term<T>()
data class AbstractionTerm<T>(val abstraction: TermAbstraction<T>): Term<T>()
data class ApplicationTerm<T>(val application: TermApplication<T>): Term<T>()
data class VariableTerm<T>(val variable: TermVariable): Term<T>()

data class TermVariable(val index: Int)
data class TermAbstraction<out T>(val term: Term<T>)
data class TermApplication<out T>(val lhs: Term<T>, val rhs: Term<T>)

fun <T> term(value: T): Term<T> = NativeTerm(value)
fun <T> term(abstraction: TermAbstraction<T>): Term<T> = AbstractionTerm(abstraction)
fun <T> term(application: TermApplication<T>): Term<T> = ApplicationTerm(application)
fun <T> term(variable: TermVariable): Term<T> = VariableTerm(variable)

fun variable(index: Int) = TermVariable(index)

// DSL
fun <T> varTerm(index: Int): Term<T> = term(variable(index))
fun <T> lambda(term: Term<T>): Term<T> = term(TermAbstraction(term))
operator fun <T> Term<T>.invoke(term: Term<T>): Term<T> = term(TermApplication(this, term))

fun <T> emptyTerm(): Term<T> = lambda(varTerm(0))

infix fun <T> Term<T>.plus(term: Term<T>): Term<T> = TODO()
val <T> Term<T>.pairFirst: Term<T> get() = TODO()
val <T> Term<T>.pairSecond: Term<T> get() = TODO()