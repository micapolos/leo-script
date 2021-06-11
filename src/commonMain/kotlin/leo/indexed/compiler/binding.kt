package leo.indexed.compiler

import leo.TypeLine

data class Binding(val typeLine: TypeLine, val isConstant: Boolean)
fun binding(typeLine: TypeLine, isConstant: Boolean): Binding = Binding(typeLine, isConstant)
fun constantBinding(typeLine: TypeLine) = binding(typeLine, isConstant = true)
fun functionBinding(typeLine: TypeLine) = binding(typeLine, isConstant = false)
