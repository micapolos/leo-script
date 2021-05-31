package leo

import leo.base.fold
import leo.base.reverse

val Value.script: Script
	get() =
		script().fold(fieldSeq.reverse) { plus(it.scriptLine) }

val Field.scriptLine: ScriptLine
	get() =
		null
			?: literalScriptLineOrNull
			?: exactScriptLine

val Field.literalScriptLineOrNull: ScriptLine?
	get() =
		null
			?: textOrNull?.let { line(literal(it)) }
			?: numberOrNull?.let { line(literal(it)) }

val Field.exactScriptLine: ScriptLine
	get() =
		name lineTo rhs.script

val Rhs.script: Script
	get() =
		when (this) {
			is FunctionRhs -> function.script
			is NativeRhs -> native.script
			is ValueRhs -> value.script
			is PatternRhs -> pattern.script
		}

val Field.scriptField: ScriptField
	get() = name fieldTo rhs.script

val Function.script: Script
	get() =
		body.script

val Body.script: Script
	get() =
		when (this) {
			is FnBody -> script("native")
			is BlockBody -> block.script
		}

val Block.script: Script
	get() =
		typedScriptOrNull ?: syntax.script

val Block.typedScriptOrNull: Script?
	get() =
		typeOrNull?.scriptName?.let { script(it lineTo syntax.script) }

val Native.script: Script
	get() =
		script(nativeName lineTo script(line(literal(any.toString()))))

infix fun String.lineTo(native: Native): ScriptLine =
	this lineTo native.script
