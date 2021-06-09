package leo.typed

import leo.Script
import leo.ScriptLine
import leo.line
import leo.lineTo
import leo.literal
import leo.map
import leo.numberName
import leo.script
import leo.textName

val Value.script: Script get() = body.script

val Body.script: Script get() =
	when (this) {
		is NativeBody -> any.nativeScript
		is StructureBody -> structure.script
	}

val Any?.nativeScript: Script get() =
	script(literal("#$this"))

val Structure.script: Script get() =
	fieldStack.map { scriptLine }.script

val Field.scriptLine: ScriptLine get() =
	null
		?: nativeScriptLineOrNull
		?: namedScriptLine

val Field.nativeScriptLineOrNull: ScriptLine? get() =
	when (name) {
		textName -> (value.body.nativeOrNull as? String)?.let { line(literal(it)) }
		numberName -> (value.body.nativeOrNull as? Double)?.let { line(literal(it)) }
		else ->	when (this) {
			textField -> line(textName)
			numberField -> line(numberName)
			else -> null
		}
	}

val Field.namedScriptLine: ScriptLine get() =
	name lineTo value.script