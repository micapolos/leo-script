package leo.natives

import leo.Dictionary
import leo.Value
import leo.binding
import leo.body
import leo.definition
import leo.dictionary
import leo.function

fun nativeDefinition(value: Value, fn: Dictionary.() -> Value) =
	definition(value, binding(dictionary().function(body(fn))))
