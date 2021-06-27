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
		}

val Field.scriptField: ScriptField
	get() = name fieldTo rhs.script

val Function.script: Script
	get() =
		binder.script

val Binder.script: Script get() =
	when (this) {
		is ApplyingBinder -> applying.script
		is BeingBinder -> being.script
		is CombiningBinder -> combining.script
		is DoingBinder -> doing.script
	}

val Binder.scriptLine: ScriptLine get() =
	name lineTo script

val BodyDoing.script: Script
	get() =
		body.script

val ValueBeing.script: Script
	get() =
		value.script

val BodyApplying.script: Script
	get() =
		body.script

val BodyCombining.script: Script
	get() =
		script(withName lineTo body.script)

val Body.script: Script
	get() =
		when (this) {
			is FnBody -> script("native")
			is BlockBody -> block.script
		}

val Native.script: Script
	get() =
		script(nativeName lineTo script(line(literal(any.toString()))))

infix fun String.lineTo(native: Native): ScriptLine =
	this lineTo native.script
