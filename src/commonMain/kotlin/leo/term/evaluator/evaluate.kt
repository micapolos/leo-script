package leo.term.evaluator

import leo.Script
import leo.term.compiler.runtime.typedValue
import leo.term.decompiler.script

val Script.evaluate: Script
	get() =
	typedValue.script
