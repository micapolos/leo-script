package leo.named.expression

import leo.Script
import leo.ScriptLine
import leo.doingName
import leo.getName
import leo.giveName
import leo.line
import leo.lineTo
import leo.literal
import leo.map
import leo.named.value.anyScriptLine
import leo.nativeName
import leo.plus
import leo.script
import leo.scriptLine
import leo.switchName
import leo.withName

val Expression.scriptLine: ScriptLine get() =
	"expression" lineTo script

val Expression.script: Script get() =
	when (this) {
		is EmptyExpression -> script()
		is GetExpression -> get.script
		is InvokeExpression -> invoke.script
		is WithExpression -> with.script
		is BindExpression -> bind.script
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

val Get.script: Script get() = expression.script.plus(getName lineTo script(name))
val Invoke.script: Script get() = function.script.plus(giveName lineTo params.script)
val Bind.script: Script get() = script("bind" lineTo script(binding.scriptLine)).plus(expression.script)
val Link.script: Script get() = expression.script.plus(line.scriptLine)
val With.script: Script get() = lhs.script.plus(withName lineTo rhs.script)
val Switch.script: Script get() = expression.script.plus(switchName lineTo cases.map { scriptLine }.script)
val Case.scriptLine: ScriptLine get() = name lineTo expression.script
val Variable.script: Script get() = script("variable" lineTo script(type.scriptLine))

val Field.scriptLine: ScriptLine get() = name lineTo expression.script
val Function.scriptLine: ScriptLine get() = doingName lineTo body.script
val Binding.scriptLine: ScriptLine get() = "binding" lineTo script(type.scriptLine, expression.scriptLine)

val Body.script: Script get() =
	when (this) {
		is ExpressionBody -> expression.script
		is FnBody -> script(nativeName lineTo script(literal(name)))
	}