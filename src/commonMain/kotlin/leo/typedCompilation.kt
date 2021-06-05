package leo

typealias TypedCompilation<T> = Stateful<TypedCompiler, T>

val Syntax.typedCompilation: TypedCompilation<Typed> get() = TODO()