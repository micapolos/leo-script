package leo25.natives

import leo25.Value
import leo25.fieldTo
import leo25.notName
import leo25.value

actual val Throwable.stackTraceValue: Value
	get() = value(notName fieldTo value("available"))