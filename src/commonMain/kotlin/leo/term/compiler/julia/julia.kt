package leo.term.compiler.julia

import leo.Literal
import leo.lineTo
import leo.literal
import leo.script

data class Julia(val string: String)
val String.julia: Julia get() = Julia(this)

val Literal.julia: Julia get() = toString().julia
val idJulia = "(x->x)".julia
val yesJulia = "(f0->f1->f0${idJulia.string})".julia
val noJulia = "(f0->f1->f1${idJulia.string})".julia
val Julia.boolean: Julia get() = "($string ? ${yesJulia.string} : ${noJulia.string})".julia

val Julia.scriptLine get() = "julia" lineTo script(literal(string))