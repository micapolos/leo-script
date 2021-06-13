package leo.named.typed

import leo.Script
import leo.ScriptLine
import leo.lineTo
import leo.named.expression.script
import leo.named.expression.scriptLine
import leo.plus
import leo.script
import leo.scriptLine

val TypedExpression.script: Script get() =
	expression.script.plus("of" lineTo type.script)

val TypedExpression.scriptLine: ScriptLine get() =
	"compiled" lineTo script(expression.scriptLine, type.scriptLine)