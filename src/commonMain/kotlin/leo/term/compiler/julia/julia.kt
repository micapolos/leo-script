package leo.term.compiler.julia

import leo.Literal

data class Julia(val string: String)
val String.julia: Julia get() = Julia(this)

val Literal.julia: Julia get() = toString().julia
val idJulia = "(x->x)".julia
val yesJulia = "(f0->f1->f0${idJulia.string})".julia
val noJulia = "(f0->f1->f1${idJulia.string})".julia
val Julia.boolean: Julia get() = "($string ? ${yesJulia.string} : ${noJulia.string})".julia