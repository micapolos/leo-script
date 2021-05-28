package leo25

import leo14.Script
import leo25.natives.nativeDictionary
import leo25.parser.scriptOrThrow

val String.interpret: String
	get() =
		scriptOrThrow
			.run { interpret }
			.string

val Script.interpret: Script
	get() =
		environment().interpret(this)

fun Environment.interpret(script: Script): Script =
	try {
		script.interpretLeo.run(this).value
	} catch (e: Throwable) {
		e.value.script
	}

val Script.interpretLeo: Leo<Script>
	get() =
		nativeDictionary.valueLeo(this).map { it.script }

