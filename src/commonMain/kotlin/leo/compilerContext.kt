package leo

data class CompilerContext(
	val privateDictionary: CompilerDictionary,
	val publicDictionary: CompilerDictionary)

val emptyCompilerContext get() = CompilerContext(compilerDictionary(), compilerDictionary())