package leo.term

import leo.get

data class Evaluator<T>(val valueFn: Scope<T>.(T) -> Value<T>)

fun <T> Evaluator<T>.value(term: Term<T>): Value<T> =
	scope<T>().valueEvaluation(term).get(this)

@Suppress("UNCHECKED_CAST")
val anyEvaluator: Evaluator<Any?> get() = Evaluator {
	(it as? (Scope<Any?>.() -> Value<Any?>))?.invoke(this)
		?: it.anyValue
}

fun anyFn(fn: Scope<Any?>.() -> Value<Any?>) = fn
