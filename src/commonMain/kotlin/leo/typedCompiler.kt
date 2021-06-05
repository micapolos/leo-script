package leo

data class TypedCompiler(
	val context: CompilerContext,
	val typedStructure: TypedStructure)

val emptyTypedCompiler get() = TypedCompiler(emptyCompilerContext, emptyTypedStructure)

fun TypedCompiler.typed(syntax: Syntax): Typed = syntax.typedCompilation.get(this)
