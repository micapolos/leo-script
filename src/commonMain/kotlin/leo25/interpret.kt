package leo25

import leo14.Script
import leo25.parser.scriptOrThrow
import leo25.prelude.preludeDictionary

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
		preludeDictionary.valueLeo(this).map { it.script }

