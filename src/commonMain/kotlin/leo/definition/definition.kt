package leo.definition

import leo.Type
import leo.expression.Expression

data class Binding(val expression: Expression, val isFunction: Boolean)
data class Definition(val type: Type, val binding: Binding)
