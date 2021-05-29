package leo.natives

import leo.Dictionary
import leo.Script
import leo.Value
import leo.binding
import leo.body
import leo.definition
import leo.dictionary
import leo.function
import leo.pattern

fun nativeDefinition(script: Script, fn: Dictionary.() -> Value) =
	definition(pattern(script), binding(dictionary().function(body(fn))))
