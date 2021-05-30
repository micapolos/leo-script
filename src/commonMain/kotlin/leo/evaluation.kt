package leo

typealias Evaluation<V> = Stateful<Environment, V>

val <T> Evaluation<T>.get: T get() = get(environment())
fun <V> evaluation(value: V): Evaluation<V> = value.stateful()
val <V> V.evaluation get() = evaluation(this)

