package leo

data class Definition(val value: Value, val binding: Binding)

fun definition(value: Value, binding: Binding) = Definition(value, binding)
