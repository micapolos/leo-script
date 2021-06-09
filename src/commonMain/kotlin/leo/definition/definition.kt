package leo.expression

import leo.Type

data class Binding(val expression: Expression, val isFunction: Boolean)
data class Definition(val type: Type, val binding: Binding)
