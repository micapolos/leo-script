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

val <T> Structure<T>.script: Script get() =
	valueStack.map { scriptLine }.script

val <T> Value<T>.scriptLine: ScriptLine get() =
	when (this) {
		is AnyValue -> any.nativeScriptLine
		is FieldValue -> field.scriptLine
		is FunctionValue -> function.scriptLine
		is LiteralValue -> literal.line
	}

val <T> T.nativeScriptLine: ScriptLine get() =
	nativeName lineTo script(literal("$this"))

val <T> Field<T>.scriptLine: ScriptLine get() =
	name lineTo structure.script

val <T> Function<T>.scriptLine get() =
	doingName lineTo script(expression.nativeScriptLine)
