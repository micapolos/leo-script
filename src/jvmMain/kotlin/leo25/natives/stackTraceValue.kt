package leo25.natives

import leo25.literal
import leo25.Value
import leo25.field
import leo25.value

actual val Throwable.stackTraceValue: Value
	get() =
		value(*stackTrace.map { field(literal(it.toString())) }.toTypedArray())

