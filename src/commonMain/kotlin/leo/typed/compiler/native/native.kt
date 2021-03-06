package leo.typed.compiler.native

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral
import leo.lineTo
import leo.literal
import leo.script

sealed class Native
data class DoubleNative(val double: Double) : Native()
data class StringNative(val string: String) : Native()
object DoublePlusDoubleNative : Native()
object DoubleMinusDoubleNative : Native()
object DoubleTimesDoubleNative : Native()
object DoubleDividedByDoubleNative : Native()
object DoubleRootNative : Native()
object DoubleSinusNative : Native()
object DoubleCosinusNative : Native()
object PiDoubleNative : Native()
object EDoubleNative : Native()
object DoubleIsLessThanDoubleNative : Native()
object DoubleStringNative : Native()
object ObjectEqualsObjectNative : Native()
object StringPlusStringNative : Native()
object StringLengthNative : Native()

val Double.native: Native get() = DoubleNative(this)
val String.native: Native get() = StringNative(this)

val Native.double: Double get() = (this as DoubleNative).double
val Native.string: String get() = (this as StringNative).string

val Literal.native: Native
  get() =
    when (this) {
      is NumberLiteral -> number.double.native
      is StringLiteral -> string.native
    }

val Native.scriptLine get() =
  "native" lineTo
    when (this) {
      is DoubleNative ->
        script("double" lineTo script(literal(double)))
      is StringNative ->
        script("string" lineTo script(literal(string)))
      DoubleIsLessThanDoubleNative ->
        script(
          "double" lineTo script(),
          "is" lineTo script("less" lineTo script("than" lineTo script("double"))))
      DoubleMinusDoubleNative ->
        script(
          "double" lineTo script(),
          "minus" lineTo script("double"))
      DoublePlusDoubleNative ->
        script(
          "double" lineTo script(),
          "minus" lineTo script("double"))
      DoubleTimesDoubleNative ->
        script(
          "double" lineTo script(),
          "times" lineTo script("double"))
      DoubleDividedByDoubleNative ->
        script(
          "double" lineTo script(),
          "divided" lineTo script(
            "by" lineTo script("double")))
      DoubleStringNative -> script("string" lineTo script("double"))
      PiDoubleNative -> script("double" lineTo script("pi"))
      EDoubleNative -> script("double" lineTo script("e"))
      DoubleRootNative -> script("root" lineTo script("double"))
      DoubleSinusNative -> script("sinus" lineTo script("double"))
      DoubleCosinusNative -> script("cosinus" lineTo script("double"))
      ObjectEqualsObjectNative ->
        script(
          "object" lineTo script(),
          "equals" lineTo script("object"))
      StringLengthNative ->
        script("length" lineTo script("string"))
      StringPlusStringNative ->
        script(
          "string" lineTo script(),
          "plus" lineTo script("string"))
    }