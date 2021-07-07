package leo.term

import leo.get

data class Evaluator<T>(val valueFn: Scope<T>.(T) -> Value<T>)

val nothingEvaluator get() = Evaluator<Nothing> { error("nothing") }

fun <T> Evaluator<T>.value(term: Term<T>): Value<T> =
	scope<T>().valueEvaluation(term).get(this)
