package leo25.natives

import leo14.Script
import leo25.Dictionary
import leo25.Value
import leo25.binding
import leo25.body
import leo25.definition
import leo25.dictionary
import leo25.function
import leo25.pattern

fun nativeDefinition(script: Script, fn: Dictionary.() -> Value) =
	definition(pattern(script), binding(dictionary().function(body(fn))))
