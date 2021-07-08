package leo.term

import leo.get

data class Evaluator<T>(val valueFn: Scope<T>.(T) -> Value<T>)

fun <T> evaluator() = Evaluator<T> { it.nativeValue }

fun <T> Evaluator<T>.value(term: Term<T>): Value<T> =
  scope<T>().valueEvaluation(term).get(this)

fun <T> Term<T>.value(evaluator: Evaluator<T>): Value<T> =
  evaluator.value(this)