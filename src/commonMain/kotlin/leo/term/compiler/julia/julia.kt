package leo.term.compiler.julia

data class Julia(val string: String)
val String.julia: Julia get() = Julia(this)

val yesJulia = "(f0->f1->f0(x->x))".julia
val noJulia = "(f0->f1->f1(x->x))".julia
