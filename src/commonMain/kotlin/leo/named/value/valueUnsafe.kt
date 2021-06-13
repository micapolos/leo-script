package leo.named.value

import leo.Literal
import leo.Number
import leo.base.notNullOrError
import leo.literal
import leo.numberOrNull
import leo.onlyOrNull
import leo.stringOrNull

fun <T> Double.numberValue(): Value<T> = value(valueLine(literal(this)))
fun <T> Int.numberValue(): Value<T> = toDouble().numberValue()
fun <T> String.textValue(): Value<T> = value(valueLine(literal(this)))

val <T> Value<T>.unsafeLine: ValueLine<T> get() =
	lineStack.onlyOrNull.notNullOrError("$this not a single line")

val <T> Value<T>.unsafeFunction: ValueFunction<T> get() =
	(unsafeLine as? FunctionValueLine<T>)?.function.notNullOrError("$this not a function")

val <T> Value<T>.unsafeLiteral: Literal get() =
	(unsafeLine as LiteralValueLine).literal

val <T> Value<T>.unsafeNumber: Number get() =
	unsafeLiteral.numberOrNull!!

val <T> Value<T>.unsafeDouble: Double get() =
	unsafeNumber.double

val <T> Value<T>.unsafeString: String get() =
	unsafeLiteral.stringOrNull!!

fun <T> Value<T>.numberPlusNumber(value: Value<T>): Value<T> =
	unsafeDouble.plus(value.unsafeDouble).numberValue()