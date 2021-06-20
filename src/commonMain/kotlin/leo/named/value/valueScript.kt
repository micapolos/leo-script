package leo.named.value

import leo.Script
import leo.ScriptLine
import leo.functionName
import leo.line
import leo.lineTo
import leo.literal
import leo.map
import leo.named.evaluator.scriptLine
import leo.named.expression.script
import leo.nativeName
import leo.script

val Value.scriptLine: ScriptLine get() =
	"value" lineTo lineStack.map { scriptLine }.script

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
	functionName lineTo script(dictionary.scriptLine, "body" lineTo doing.script)
