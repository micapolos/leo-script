package leo.typed

import leo.Script
import leo.ScriptLine
import leo.line
import leo.lineTo
import leo.literal
import leo.map
import leo.nativeName
import leo.numberName
import leo.script
import leo.textName

val Value.script: Script get() =
	when (this) {
		is NativeValue -> any.nativeScript
		is StructureValue -> structure.script
	}

val Any?.nativeScript: Script get() =
	script(nativeName lineTo script(literal(toString())))

val Structure.script: Script get() =
	fieldStack.map { scriptLine }.script

val Field.scriptLine: ScriptLine get() =
	null
		?: nativeScriptLineOrNull
		?: namedScriptLine

val Field.nativeScriptLineOrNull: ScriptLine? get() =
	when (name) {
		textName -> (value.nativeOrNull as? String)?.let { line(literal(it)) }
		numberName -> (value.nativeOrNull as? Double)?.let { line(literal(it)) }
		else -> null
	}

val Field.namedScriptLine: ScriptLine get() =
	name lineTo value.script