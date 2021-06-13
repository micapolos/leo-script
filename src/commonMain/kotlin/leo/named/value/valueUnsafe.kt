package leo.named.value

import leo.Literal
import leo.Number
import leo.base.notNullOrError
import leo.literal
import leo.numberOrNull
import leo.onlyOrNull
import leo.stringOrNull

val Double.numberValue get() = value(valueLine(literal(this)))
val Int.numberValue get() = toDouble().numberValue
val String.textValue get() = value(valueLine(literal(this)))

val Any?.anyValue: Value get() = value(anyValueLine(this))

val Value.unsafeLine: ValueLine get() =
	lineStack.onlyOrNull.notNullOrError("$this not a single line")

val Value.unsafeFunction: ValueFunction get() =
	(unsafeLine as? FunctionValueLine)?.function.notNullOrError("$this not a function")

val Value.unsafeLiteral: Literal get() =
	(unsafeLine as LiteralValueLine).literal

val Value.unsafeNumber: Number get() =
	unsafeLiteral.numberOrNull!!

val Value.unsafeDouble: Double get() =
	unsafeNumber.double

val Value.unsafeString: String get() =
	unsafeLiteral.stringOrNull!!

val ValueLine.unsafeAny: Any? get() =
	(this as AnyValueLine).any

val Value.unsafeInt: Int get() =
	(unsafeLine.unsafeAny as Int)

fun Value.intPlusInt(value: Value): Value =
	unsafeInt.plus(value.unsafeInt).anyValue

fun Value.numberPlusNumber(value: Value): Value =
	unsafeDouble.plus(value.unsafeDouble).numberValue
