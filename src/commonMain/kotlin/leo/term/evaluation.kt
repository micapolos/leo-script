package leo.term

import leo.Stateful
import leo.bind
import leo.getStateful
import leo.map
import leo.stateful

typealias Evaluation<T, V> = Stateful<Evaluator<T>, V>

fun <T, V> V.evaluation(): Evaluation<T, V> = stateful()
fun <T> evaluatorEvaluation(): Evaluation<T, Evaluator<T>> = getStateful()

fun <T> Scope<T>.valueEvaluation(term: Term<T>): Evaluation<T, Value<T>> =
  when (term) {
    is AbstractionTerm -> valueEvaluation(term.abstraction)
    is ApplicationTerm -> valueEvaluation(term.application)
    is NativeTerm -> valueEvaluation(term.native)
    is VariableTerm -> valueEvaluation(term.variable)
  }

fun <T> Scope<T>.valueEvaluation(abstraction: TermAbstraction<T>): Evaluation<T, Value<T>> =
  value(function(this, abstraction.term)).evaluation()

fun <T> Scope<T>.valueEvaluation(application: TermApplication<T>): Evaluation<T, Value<T>> =
  valueEvaluation(application.lhs).bind { lhsValue ->
    valueEvaluation(application.rhs).bind { rhsValue ->
      lhsValue.invokeEvaluation(rhsValue)
    }
  }

fun <T> Scope<T>.valueEvaluation(variable: TermVariable): Evaluation<T, Value<T>> =
  value(variable).evaluation()

fun <T> Scope<T>.valueEvaluation(native: T): Evaluation<T, Value<T>> =
  evaluatorEvaluation<T>().map { it.valueFn(this, native) }