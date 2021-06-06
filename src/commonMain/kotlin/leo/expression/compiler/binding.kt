package leo.expression.compiler

import leo.TypeLine

data class Binding(val typeLine: TypeLine, val isFunction: Boolean)

val TypeLine.constantBinding: Binding get() = Binding(this, isFunction = false)
val TypeLine.functionBinding: Binding get() = Binding(this, isFunction = true)
