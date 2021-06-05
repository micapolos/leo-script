package leo

typealias TermCompilation<T> = Stateful<TermCompiler<T>, Term<T>>

fun <T> Typed.termCompilation(): TermCompilation<T> = TODO()

