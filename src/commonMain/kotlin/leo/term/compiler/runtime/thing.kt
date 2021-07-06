package leo.term.compiler.runtime

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral

sealed class Native
data class DoubleNative(val double: Double): Native()
data class StringNative(val string: String): Native()
object DoubleAddDoubleNative: Native()
object DoubleSubtractDoubleNative: Native()
object DoubleMultiplyByDoubleNative: Native()
object StringAppendStringNative: Native()

val Double.native: Native get() = DoubleNative(this)
val String.native: Native get() = StringNative(this)

val Native.double: Double get() = (this as DoubleNative).double
val Native.string: String get() = (this as StringNative).string

val Literal.native: Native get() =
	when (this) {
		is NumberLiteral -> number.double.native
		is StringLiteral -> string.native
	}