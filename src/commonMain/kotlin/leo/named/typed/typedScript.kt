package leo.named.typed

import leo.Script
import leo.lineTo
import leo.named.expression.script
import leo.plus
import leo.script

val TypedExpression.script: Script get() =
	expression.script.plus("of" lineTo type.script)
