package leo.typed.indexed

data class Evaluator<V>(
  val invokeFn: V.(Array<out Value<V>>) -> Value<V>,
  val nativeValueEvaluationFn: V.(ValueScope<V>) -> Evaluation<V, Value<V>>)

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
val incEvaluator: Evaluator<Int> get() =
  Evaluator(
    { nativeValue(plus(it.size)) },
    { scope -> nativeValue(this).evaluation() })
