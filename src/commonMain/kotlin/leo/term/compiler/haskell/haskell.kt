package leo.term.compiler.haskell

data class Haskell(val string: String)

val String.haskell: Haskell get() = Haskell(this)
