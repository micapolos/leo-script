package leo.named.typed

import leo.Script
import leo.ScriptLine
import leo.lineTo
import leo.named.expression.script
import leo.plus
import leo.script

val TypedExpression.script: Script get() =
	expression.script.plus("of" lineTo type.script)

val TypedExpression.scriptLine: ScriptLine get() =
	"typed" lineTo script