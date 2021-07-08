package leo.interactive

import leo.Literal

data class NameBegin(val name: String)
object End

sealed class Token
data class BeginToken(val begin: NameBegin) : Token()
data class EndToken(val end: End) : Token()
data class LiteralToken(val literal: Literal) : Token()

fun begin(name: String) = NameBegin(name)
val end get() = End

fun token(begin: NameBegin): Token = BeginToken(begin)
fun token(end: End): Token = EndToken(end)
fun token(literal: Literal): Token = LiteralToken(literal)