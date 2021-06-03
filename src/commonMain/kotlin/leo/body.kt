package leo

sealed class Body
data class CodeBody(val code: Code) : Body()
data class FnBody(val fn: (Dictionary) -> Value) : Body()

fun body(code: Code): Body = CodeBody(code)
fun body(fn: Dictionary.() -> Value): Body = FnBody(fn)
fun body(script: Script): Body = body(code(script.syntax))
