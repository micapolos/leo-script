package leo

typealias Syntaxing<T> = Stateful<Unit, T>
val <T> T.syntaxing: Syntaxing<T> get() = stateful()
val <T> Syntaxing<T>.get get() = run(Unit).value