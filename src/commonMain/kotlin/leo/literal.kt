package leo

import leo.base.appendableString

sealed class Literal

data class StringLiteral(val string: String) : Literal() {
  override fun toString() = string.literalString
}

data class NumberLiteral(val number: Number) : Literal() {
  override fun toString() = "$number"
}

val Literal.stringOrNull get() = (this as? StringLiteral)?.string
val Literal.numberOrNull get() = (this as? NumberLiteral)?.number

fun literal(string: String): Literal = StringLiteral(string)
fun literal(number: Number): Literal = NumberLiteral(number)
fun literal(int: Int): Literal = literal(number(int))
fun literal(long: Long): Literal = literal(number(long))
fun literal(double: Double): Literal = literal(number(double))

val String.literal get() = literal(this)
val Int.literal get() = literal(this)
val Double.literal get() = literal(this)
val Number.literal get() = literal(this)

val Any.anyLiteral: Literal
  get() =
    when (this) {
      is String -> literal(this)
      is Int -> literal(this)
      is Double -> literal(this)
      else -> error("")
    }

val Literal.reflectScriptLine
  get() =
    "literal" lineTo script(this)

// TODO: Implement proper escaping.
val String.literalString
  get() =
    appendableString { appendable ->
      appendable
        .append("\"")
        .append(this.replace("\n", "\\n").replace("\"", "\\\""))
        .append("\"")
    }

val Int.literalString: String get() = "$this"
