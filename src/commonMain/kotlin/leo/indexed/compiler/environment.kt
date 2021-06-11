package leo.indexed.compiler

data class Environment<out T>(val fn: () -> Unit)

val unitEnvironment: Environment<Unit> get() = Environment { Unit }