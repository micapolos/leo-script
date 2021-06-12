package leo.named.compiler

data class Environment<out T>(val unit: Unit)

val unitEnvironment: Environment<Unit> get() = Environment(Unit)