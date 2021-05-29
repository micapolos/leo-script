package leo25

sealed class Body
data class BlockBody(val block: Block) : Body()
data class FnBody(val fn: (Dictionary) -> Value) : Body()

fun body(block: Block): Body = BlockBody(block)
fun body(fn: Dictionary.() -> Value): Body = FnBody(fn)
fun body(script: Script): Body = body(block(script))
