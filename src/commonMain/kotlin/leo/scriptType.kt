package leo

val Script.type: Type get() = emptyTypeCompiler.compilation(this)
