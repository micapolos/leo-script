package leo.indexed.compiler

data class Context<out T>(
	val environment: Environment<T>)
