package leo.term.compiler.haskell

import leo.Literal
import leo.lineTo
import leo.literal
import leo.script

data class Haskell(val string: String)

val String.haskell: Haskell get() = Haskell(this)

val Literal.haskell: Haskell get() = toString().haskell

val idHaskell: Haskell get() = "id".haskell
val trueHaskell: Haskell get() = "(\\t f -> t(${idHaskell.string}))".haskell
val falseHaskell: Haskell get() = "(\\t f -> f(${idHaskell.string}))".haskell
val Haskell.boolean: Haskell get() = "(if $string then ${trueHaskell.string} else ${falseHaskell.string})".haskell

val Haskell.scriptLine get() = "haskell" lineTo script(literal(string))