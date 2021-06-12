package leo.named.evaluator

import leo.TypeStructure

data class Definition<out T>(val typeStructure: TypeStructure, val binding: Binding<T>)

fun <T> definition(typeStructure: TypeStructure, binding: Binding<T>) =
	Definition(typeStructure, binding)
