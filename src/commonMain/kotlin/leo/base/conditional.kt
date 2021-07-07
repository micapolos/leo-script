package leo.base

data class Conditional<out V>(val boolean: Boolean, val v: V)
fun <V> Boolean.conditional(v: V) = Conditional(this, v)
