package leo.term.compiled.codegen

data class Scope(val depth: Int)

val Scope.push: Scope get() = Scope(depth.inc())
