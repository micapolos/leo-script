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

val <T> Value<T>.script: Script get() =
	lineStack.map { scriptLine }.script

val <T> ValueLine<T>.scriptLine: ScriptLine get() =
	when (this) {
		is AnyValueLine -> any.nativeScriptLine
		is FieldValueLine -> field.scriptLine
		is FunctionValueLine -> function.scriptLine
		is LiteralValueLine -> literal.line
	}

val <T> T.nativeScriptLine: ScriptLine get() =
	nativeName lineTo script(literal("$this"))

val <T> ValueField<T>.scriptLine: ScriptLine get() =
	name lineTo value.script

val <T> ValueFunction<T>.scriptLine get() =
	doingName lineTo script(body.nativeScriptLine)
