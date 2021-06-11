package leo.indexed.evaluator

import leo.Script
import leo.ScriptLine
import leo.indexed.compiler.typed

val Script.evaluateValue: Value get() =
	typed.expression.evaluate

val Script.evaluateLine: ScriptLine get() =
	typed.let { typed ->
		typed.typeLine.scriptLine(typed.expression.evaluate)
	}
