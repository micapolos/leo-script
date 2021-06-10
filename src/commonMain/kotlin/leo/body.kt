package leo

sealed class Body
data class BlockBody(val block: Block) : Body()
data class FnBody(val fn: (Value) -> Value) : Body()

fun body(block: Block): Body = BlockBody(block)
fun body(fn: Value.() -> Value): Body = FnBody(fn)
fun body(script: Script): Body = body(block(script.syntax))

@kotlin.jvm.JvmInline value class BodyRecurse(val body: Body)

fun recurse(function: Body) = BodyRecurse(function)
