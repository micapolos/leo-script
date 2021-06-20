package leo.named.expression

import leo.Literal
import leo.Script
import leo.ScriptLine
import leo.beName
import leo.bindName
import leo.doName
import leo.doingName
import leo.expressionName
import leo.getName
import leo.giveName
import leo.letName
import leo.line
import leo.lineTo
import leo.literal
import leo.makeName
import leo.map
import leo.named.value.anyScriptLine
import leo.nativeName
import leo.natives.invokeName
import leo.plus
import leo.privateName
import leo.recursiveName
import leo.script
import leo.switchName
import leo.toName
import leo.withName

val Expression.scriptLine: ScriptLine get() =
	expressionName lineTo script

val Expression.script: Script get() =
	lineStack.map { scriptLine }.script

val Line.scriptLine: ScriptLine get() =
	when (this) {
		is AnyLine -> any.anyScriptLine
		is BeLine -> be.scriptLine
		is BindLine -> bind.scriptLine
		is DoLine -> do_.scriptLine
		is DoingLine -> doing.scriptLine
		is FieldLine -> field.scriptLine
		is GetLine -> get.scriptLine
		is GiveLine -> give.scriptLine
		is InvokeLine -> invoke.scriptLine
		is LetLine -> let.scriptLine
		is LiteralLine -> literal.scriptLine
		is MakeLine -> make.scriptLine
		is PrivateLine -> private.scriptLine
		is RecursiveLine -> recursive.scriptLine
		is SwitchLine -> switch.scriptLine
		is WithLine -> with.scriptLine
	}

val Be.scriptLine: ScriptLine get() = beName lineTo expression.script
val Bind.scriptLine: ScriptLine get() = bindName lineTo expression.script
val Case.scriptLine: ScriptLine get() = name lineTo expression.script
val Do.scriptLine: ScriptLine get() = doName lineTo body.script
val Doing.scriptLine: ScriptLine get() = doingName lineTo type.script.plus(toName lineTo body.script)
val Field.scriptLine: ScriptLine get() = name lineTo expression.script
val Get.scriptLine: ScriptLine get() = getName lineTo script(name)
val Give.scriptLine: ScriptLine get() = giveName lineTo expression.script
val Invoke.scriptLine: ScriptLine get() = invokeName lineTo type.script
val Let.scriptLine: ScriptLine get() = letName lineTo type.script.plus(rhs.scriptLine)
val Literal.scriptLine: ScriptLine get() = line(this)
val Make.scriptLine: ScriptLine get() = makeName lineTo script(name)
val Switch.scriptLine: ScriptLine get() = switchName lineTo cases.map { scriptLine }.script
val Private.scriptLine: ScriptLine get() = privateName lineTo expression.script
val Recursive.scriptLine: ScriptLine get() = recursiveName lineTo expression.script
val With.scriptLine: ScriptLine get() = withName lineTo expression.script

val LetRhs.scriptLine: ScriptLine get() =
	when (this) {
		is BeLetRhs -> be.scriptLine
		is DoLetRhs -> do_.scriptLine
	}

val Body.script: Script get() =
	when (this) {
		is ExpressionBody -> expression.script
		is FnBody -> script(nativeName lineTo script(literal(name)))
	}