package leo.indexed.compiler

sealed class Definition<out T>
data class LetDefinition<T>(val let: Let<T>): Definition<T>()
data class RecursiveDefinition<T>(val recursive: Recursive<T>): Definition<T>()
