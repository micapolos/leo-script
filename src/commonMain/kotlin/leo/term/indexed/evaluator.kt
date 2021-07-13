package leo.term.indexed

data class Evaluator<V>(val valueFn: ValueScope<V>.(V) -> Value<V>)

val incEvaluator: Evaluator<Int> get() = Evaluator { nativeValue(it.inc()) }
val nothingEvaluator: Evaluator<Nothing> get() = Evaluator { error("nothing") }