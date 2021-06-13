package leo.named.expression

import leo.Script
import leo.ScriptLine
import leo.doingName
import leo.getName
import leo.giveName
import leo.line
import leo.lineTo
import leo.map
import leo.named.value.anyScriptLine
import leo.plus
import leo.script
import leo.switchName
import leo.toName

val Expression.script: Script get() =
	when (this) {
		is EmptyExpression -> script()
		is GetExpression -> get.script
		is InvokeExpression -> invoke.script
		is LinkExpression -> link.script
		is SwitchExpression -> switch.script
		is VariableExpression -> variable.script
	}

val Line.scriptLine: ScriptLine get() =
	when (this) {
		is AnyLine -> any.anyScriptLine
		is FieldLine -> field.scriptLine
		is FunctionLine -> function.scriptLine
		is LiteralLine -> line(literal)
	}

val Get.script: Script get() = script(getName lineTo script(name))
val Invoke.script: Script get() = function.script.plus(giveName lineTo params.script)
val Link.script: Script get() = expression.script.plus(line.scriptLine)
val Switch.script: Script get() = expression.script.plus(switchName lineTo cases.map { scriptLine }.script)
val Case.scriptLine: ScriptLine get() = name lineTo expression.script
val Variable.script: Script get() = script("variable" lineTo type.script)

val Field.scriptLine: ScriptLine get() = name lineTo expression.script
val Function.scriptLine: ScriptLine get() = doingName lineTo paramType.script.plus(toName lineTo body.script)

val Body.script: Script get() =
	when (this) {
		is ExpressionBody -> expression.script
		is FnBody -> script(valueFn.anyScriptLine)
	}