package leo.natives

import leo.Value
import leo.field
import leo.literal
import leo.value

actual val Throwable.stackTraceValue: Value
  get() =
    value(*stackTrace.map { field(literal(it.toString())) }.toTypedArray())

