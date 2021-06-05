package leo

data class CompilerDictionary(val definitionStack: Stack<CompilerDefinition>)

val Stack<CompilerDefinition>.compilerDictionary get() = CompilerDictionary(this)
fun compilerDictionary(vararg definitions: CompilerDefinition) = stack(*definitions).compilerDictionary
fun CompilerDictionary.plus(definition: CompilerDefinition) = definitionStack.push(definition).compilerDictionary