package leo25.parser

import leo14.Script

val String.scriptOrNull: Script?
	get() =
		preprocessingScriptParser.parsed(this)

val String.scriptOrThrow: Script
	get() =
		preprocessingScriptParser.parseOrThrow(this)
