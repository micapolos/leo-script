package leo

sealed class Body
data class CodeBody(val block: Block) : Body()
data class FnBody(val fn: (Dictionary) -> Value) : Body()

fun body(block: Block): Body = CodeBody(block)
fun body(fn: Dictionary.() -> Value): Body = FnBody(fn)
fun body(script: Script): Body = body(block(script.syntax))
