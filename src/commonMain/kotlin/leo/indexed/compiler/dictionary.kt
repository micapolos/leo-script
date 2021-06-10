package leo.indexed.compiler

import leo.Stack
import leo.stack

data class Dictionary<out T>(val definitionStack: Stack<Definition<T>>)
fun <T> dictionary(vararg definitions: Definition<T>) = Dictionary(stack(*definitions))