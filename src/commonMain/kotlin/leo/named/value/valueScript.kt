package leo.named.value

import leo.Script
import leo.ScriptLine
import leo.doingName
import leo.line
import leo.lineTo
import leo.literal
import leo.map
import leo.nativeName
import leo.script

val Value.script: Script get() =
	lineStack.map { scriptLine }.script

val ValueLine.scriptLine: ScriptLine get() =
	when (this) {
		is AnyValueLine -> any.anyScriptLine
		is FieldValueLine -> field.scriptLine
		is FunctionValueLine -> function.scriptLine
	}

val Any?.anyScriptLine: ScriptLine get() =
	when (this) {
		is String -> line(literal(this))
		is Double -> line(literal(this))
		else -> nativeName lineTo script(literal("$this"))
	}

val ValueField.scriptLine: ScriptLine get() =
	name lineTo value.script

val ValueFunction.scriptLine get() =
	doingName lineTo script(body.anyScriptLine)
