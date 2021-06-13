package leo.named.compiler

data class Environment(val unit: Unit)

val unitEnvironment: Environment get() = Environment(Unit)