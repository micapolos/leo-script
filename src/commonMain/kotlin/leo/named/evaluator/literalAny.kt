package leo.named.evaluator

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral

val Literal.any: Any?
  get() =
    when (this) {
      is NumberLiteral -> number.double
      is StringLiteral -> string
    }