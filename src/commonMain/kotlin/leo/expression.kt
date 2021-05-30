package leo

// TODO: Refactor to proper expression model.
data class Expression(val script: Script)
fun expression(script: Script) = Expression(script.syntax.script) // TODO: For validation