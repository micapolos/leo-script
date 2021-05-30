package leo

typealias Compilation<T> = Stateful<Unit, T>
val <T> T.compilation: Compilation<T> get() = stateful()
val <T> Compilation<T>.get get() = run(Unit).value