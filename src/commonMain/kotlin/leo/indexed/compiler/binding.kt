package leo.indexed.compiler

import leo.Type

data class Binding<out T>(val type: Type, val isConstant: Boolean)
