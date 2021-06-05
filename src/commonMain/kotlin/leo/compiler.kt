package leo

data class Compiler(
	val context: CompilerContext,
	val typedStructure: TypedStructure)

val emptyTypedCompiler get() = Compiler(emptyCompilerContext, emptyTypedStructure)

fun Compiler.typed(syntax: Syntax): Typed = syntax.compilation.get(this)
