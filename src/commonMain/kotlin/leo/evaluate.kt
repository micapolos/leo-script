package leo

import leo.parser.scriptOrThrow
import leo.prelude.preludeDictionary

val String.evaluate: String
	get() =
		scriptOrThrow
			.run { evaluate }
			.string

val Script.evaluate: Script
	get() =
		environment().evaluate(this)

fun Environment.evaluate(script: Script): Script =
	try {
		script.evaluateEvaluation.run(this).value
	} catch (e: Throwable) {
		e.value.errorValue.script
	}

val Script.evaluateEvaluation: Evaluation<Script>
	get() =
		preludeDictionary.valueEvaluation(syntax).map { it.script }
