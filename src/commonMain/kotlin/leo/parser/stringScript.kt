package leo.parser

import leo.Script

val String.scriptOrNull: Script?
	get() =
		preprocessingScriptParser.parsed(this)

val String.scriptOrThrow: Script
	get() =
		preprocessingScriptParser.parseOrThrow(this)
