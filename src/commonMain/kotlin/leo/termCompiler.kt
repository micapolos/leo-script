package leo

data class TermCompiler<T>(
  val typedTermOrNullFn: (Typed) -> Term<T>?,
  val literalTermFn: (Literal) -> Term<T>
)

fun <T> TermCompiler<T>.term(typed: Typed): Term<T> =
  typed.termCompilation<T>().run(this).value
