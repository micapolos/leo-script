package leo.term

import leo.Empty
import leo.named.value.anyScriptLine

sealed class Term<out T> {
  override fun toString() = scriptLine { it.anyScriptLine }.toString()
}

data class EmptyTerm<T>(val empty: Empty): Term<T>() {
  override fun toString() = super.toString()
}

data class NativeTerm<T>(val native: T) : Term<T>() {
  override fun toString() = super.toString()
}

data class AbstractionTerm<T>(val abstraction: TermAbstraction<T>) : Term<T>() {
  override fun toString() = super.toString()
}

data class ApplicationTerm<T>(val application: TermApplication<T>) : Term<T>() {
  override fun toString() = super.toString()
}

data class VariableTerm<T>(val variable: IndexVariable) : Term<T>() {
  override fun toString() = super.toString()
}

data class IndexVariable(val index: Int) {
  override fun toString() = script.toString()
}

data class TermAbstraction<out T>(val term: Term<T>) {
  override fun toString() = script { it.anyScriptLine }.toString()
}

data class TermApplication<out T>(val lhs: Term<T>, val rhs: Term<T>) {
  override fun toString() = script { it.anyScriptLine }.toString()
}

fun <T> term(empty: Empty): Term<T> = EmptyTerm(empty)
fun <T> nativeTerm(value: T): Term<T> = NativeTerm(value)
fun <T> term(abstraction: TermAbstraction<T>): Term<T> = AbstractionTerm(abstraction)
fun <T> term(application: TermApplication<T>): Term<T> = ApplicationTerm(application)
fun <T> term(variable: IndexVariable): Term<T> = VariableTerm(variable)

val <T> Term<T>.applicationOrNull: TermApplication<T>? get() = (this as? ApplicationTerm)?.application
val <T> Term<T>.abstractionOrNull: TermAbstraction<T>? get() = (this as? AbstractionTerm)?.abstraction
val <T> Term<T>.variableOrNull: IndexVariable? get() = (this as? VariableTerm)?.variable
val <T : Any> Term<T>.nativeOrNull: T? get() = (this as? NativeTerm)?.native

fun variable(index: Int) = IndexVariable(index)
val Int.variable get() = IndexVariable(this)

// DSL
val <T> T.nativeTerm: Term<T> get() = NativeTerm(this)
fun <T> get(index: Int): Term<T> = term(variable(index))
fun <T> fn(term: Term<T>): Term<T> = term(TermAbstraction(term))
fun <T> Term<T>.invoke(term: Term<T>): Term<T> = term(TermApplication(this, term))
fun <T> id(): Term<T> = fn(get(0))

fun <T> Term<T>.plus(term: Term<T>): Term<T> =
  fn(fn(fn(get<T>(0).invoke(get(2)).invoke(get(1))))).invoke(this).invoke(term)

val <T> Term<T>.tail: Term<T> get() = invoke(fn(fn(get(1))))
val <T> Term<T>.head: Term<T> get() = invoke(fn(fn(get(0))))

val <T> Term<T>.eitherFirst: Term<T> get() = fn(fn(fn(get<T>(1).invoke(get(2))))).invoke(this)
val <T> Term<T>.eitherSecond: Term<T> get() = fn(fn(fn(get<T>(0).invoke(get(2))))).invoke(this)

fun <T> fix(): Term<T> =
  fn(
    fn(
      get<T>(1)
        .invoke(
          fn(
            get<T>(1)
              .invoke(get(1))
              .invoke(get(0)))))
      .invoke(
        fn(get<T>(1)
          .invoke(
            fn(get<T>(1)
              .invoke(get(1))
              .invoke(get(0)))))))
