package leo.term.compiler.idris

data class Idris(val string: String)

val String.idris: Idris get() = Idris(this)
