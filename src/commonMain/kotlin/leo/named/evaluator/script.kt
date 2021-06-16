package leo.named.evaluator

import leo.ScriptLine
import leo.lineTo
import leo.map
import leo.named.value.scriptLine
import leo.recursiveName
import leo.script
import leo.scriptLine

val Dictionary.scriptLine: ScriptLine get() =
	"dictionary" lineTo script(
		"definitions" lineTo definitionStack.map { scriptLine }.script)

val Definition.scriptLine: ScriptLine get() =
	"definition" lineTo script(type.scriptLine, binding.scriptLine)

val Binding.scriptLine: ScriptLine get() =
	when (this) {
		is ValueBinding -> value.scriptLine
		is RecursiveBinding -> recursive.scriptLine
		is FunctionBinding -> function.scriptLine
	}

val Recursive.scriptLine: ScriptLine get() =
	recursiveName lineTo script(recursiveDictionary.scriptLine)