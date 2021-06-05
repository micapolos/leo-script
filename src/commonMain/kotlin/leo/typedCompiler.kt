package leo

object TypedCompiler

val emptyTypedCompiler get() = TypedCompiler

fun TypedCompiler.typed(syntax: Syntax): Typed =
	syntax.typedCompilation.get(this)