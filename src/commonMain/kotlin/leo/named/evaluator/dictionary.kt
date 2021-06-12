package leo.named.evaluator

import leo.Stack
import leo.push

data class Dictionary<out T>(val definitionStack: Stack<Definition<T>>)

fun <T> Dictionary<T>.plus(definition: Definition<T>) =
	definitionStack.push(definition).let(::Dictionary)