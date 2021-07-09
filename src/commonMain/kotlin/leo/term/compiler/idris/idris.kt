package leo.term.compiler.idris

import leo.lineTo
import leo.literal
import leo.script

data class Idris(val string: String)

val String.idris: Idris get() = Idris(this)

val Idris.scriptLine get() = "idris" lineTo script(literal(string))