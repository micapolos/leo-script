package leo.indexed.compiler

data class Context<out T>(
	val environment: Environment<T>,
	val dictionary: Dictionary<T>)

val <T> Environment<T>.context get() = Context(this, dictionary())