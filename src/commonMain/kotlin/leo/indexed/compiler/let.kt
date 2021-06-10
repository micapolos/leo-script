package leo.indexed.compiler

import leo.Type

data class Let<out T>(val type: Type, val binding: Binding<T>)