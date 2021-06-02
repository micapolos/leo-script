package leo.natives

import leo.Dictionary
import leo.Script
import leo.Value
import leo.binding
import leo.body
import leo.definition
import leo.dictionary
import leo.function
import leo.type

fun nativeDefinition(script: Script, fn: Dictionary.() -> Value) =
	definition(script.type, binding(dictionary().function(body(fn))))
