package leo

val Script.presentOptionLine: ScriptLine get() =
	optionName lineTo script(presentName lineTo this)

val Script.presentOption: Script get() =
	script(presentOptionLine)

val absentOptionScriptLine: ScriptLine get() =
	optionName lineTo script(absentName)

val absentOptionScript: Script get() =
	script(absentOptionScriptLine)
