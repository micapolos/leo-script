package leo.term.compiled

data class Scope(val depth: Int)
fun scope(depth: Int = 0) = Scope(depth)
val Scope.push: Scope get() = Scope(depth.inc())
