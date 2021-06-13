package leo.named.value

import leo.base.notNullOrError
import leo.onlyOrNull

fun numberValueLine(double: Double): ValueLine = anyValueLine(double)
fun numberValueLine(int: Int): ValueLine = numberValueLine(int.toDouble())
fun textValueLine(string: String): ValueLine = anyValueLine(string)

fun numberValue(double: Double): Value = value(numberValueLine(double))
fun numberValue(int: Int): Value = value(numberValueLine(int))
fun textValue(string: String): Value = value(textValueLine(string))

val Double.numberValue get() = value(anyValueLine(this))
val Int.numberValue get() = toDouble().numberValue
val String.textValue get() = value(anyValueLine(this))

val Any?.anyValueLine: ValueLine get() = anyValueLine(this)
val Any?.anyValue: Value get() = value(anyValueLine)

val Value.unsafeLine: ValueLine get() =
	lineStack.onlyOrNull.notNullOrError("$this not a single line")

val Value.unsafeFunction: ValueFunction get() =
	(unsafeLine as? FunctionValueLine)?.function.notNullOrError("$this not a function")

val Value.unsafeAny: Any? get() =
	unsafeLine.unsafeAny

val Value.unsafeDouble: Double get() =
	unsafeAny as Double

val Value.unsafeString: String get() =
	unsafeAny as String

val ValueLine.unsafeAny: Any? get() =
	(this as AnyValueLine).any

val Value.unsafeInt: Int get() =
	(unsafeLine.unsafeAny as Int)

fun Value.intPlusInt(value: Value): Value =
	unsafeInt.plus(value.unsafeInt).anyValue

fun Value.numberPlusNumber(value: Value): Value =
	unsafeDouble.plus(value.unsafeDouble).numberValue
