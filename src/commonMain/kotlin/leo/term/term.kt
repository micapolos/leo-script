package leo.term

sealed class Term<out T> { override fun toString() = scriptLine.toString() }
data class NativeTerm<T>(val value: T): Term<T>() { override fun toString() = super.toString() }
data class AbstractionTerm<T>(val abstraction: TermAbstraction<T>): Term<T>() { override fun toString() = super.toString() }
data class ApplicationTerm<T>(val application: TermApplication<T>): Term<T>() { override fun toString() = super.toString() }
data class VariableTerm<T>(val variable: TermVariable): Term<T>() { override fun toString() = super.toString() }

data class TermVariable(val index: Int) { override fun toString() = script.toString() }
data class TermAbstraction<out T>(val term: Term<T>) { override fun toString() = script.toString() }
data class TermApplication<out T>(val lhs: Term<T>, val rhs: Term<T>) { override fun toString() = script.toString() }

fun <T> term(value: T): Term<T> = NativeTerm(value)
fun <T> term(abstraction: TermAbstraction<T>): Term<T> = AbstractionTerm(abstraction)
fun <T> term(application: TermApplication<T>): Term<T> = ApplicationTerm(application)
fun <T> term(variable: TermVariable): Term<T> = VariableTerm(variable)

fun variable(index: Int) = TermVariable(index)
val Int.variable get() = TermVariable(this)

// DSL
val <T> T.nativeTerm: Term<T> get() = NativeTerm(this)
fun <T> get(index: Int): Term<T> = term(variable(index))
fun <T> fn(term: Term<T>): Term<T> = term(TermAbstraction(term))
fun <T> Term<T>.invoke(term: Term<T>): Term<T> = term(TermApplication(this, term))
fun <T> id(): Term<T> = fn(get(0))

val Any?.anyTerm: Term<Any?> get() = nativeTerm

fun <T> Term<T>.plus(term: Term<T>): Term<T> =
	fn(fn(fn(get<T>(0).invoke(get(2)).invoke(get(1))))).invoke(this).invoke(term)

val <T> Term<T>.tail: Term<T> get() =
	invoke(fn(fn(get(1))))

val <T> Term<T>.head: Term<T> get() =
	invoke(fn(fn(get(0))))
