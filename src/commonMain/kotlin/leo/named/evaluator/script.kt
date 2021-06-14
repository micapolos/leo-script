package leo.named.evaluator

import leo.ScriptLine
import leo.lineTo
import leo.map
import leo.named.value.scriptLine
import leo.script
import leo.scriptLine

val Dictionary.scriptLine: ScriptLine get() =
	"dictionary" lineTo script(
		"definitions" lineTo definitionStack.map { scriptLine }.script)

val Definition.scriptLine: ScriptLine get() =
	"definition" lineTo script(type.scriptLine, value.scriptLine)
