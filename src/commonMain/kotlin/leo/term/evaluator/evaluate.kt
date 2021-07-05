package leo.term.evaluator

import leo.Script
import leo.term.compiler.runtime.script
import leo.term.compiler.runtime.typedValue

val Script.evaluate: Script
	get() =
	typedValue.script
