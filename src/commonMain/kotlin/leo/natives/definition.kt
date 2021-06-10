package leo.natives

import leo.Value
import leo.binding
import leo.body
import leo.definition

fun nativeDefinition(value: Value, fn: (Value) -> Value) =
	definition(value, binding(body(fn)))
