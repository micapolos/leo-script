package leo

object TypedCompiler

fun TypedCompiler.typed(syntax: Syntax): Typed =
	syntax.typedCompilation.get(this)