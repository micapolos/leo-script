package leo

typealias Leo<V> = Stateful<Environment, V>

val <T> Leo<T>.get: T get() = get(environment())
fun <V> leo(value: V): Leo<V> = value.stateful()
val <V> V.leo get() = leo(this)

