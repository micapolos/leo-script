package leo.term.indexed

data class Evaluator<V>(val invokeFn: V.(Array<out Value<V>>) -> Value<V>)

val incEvaluator: Evaluator<Int> get() = Evaluator { nativeValue(plus(it.size)) }
