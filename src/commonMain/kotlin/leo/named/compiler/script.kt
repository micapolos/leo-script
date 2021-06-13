package leo.named.compiler

import leo.ScriptLine
import leo.lineTo
import leo.map
import leo.named.typed.script
import leo.plus
import leo.script

val Dictionary.scriptLine: ScriptLine get() =
	"dictionary" lineTo definitionStack.map { scriptLine }.script

val Definition.scriptLine: ScriptLine get() =
	"let" lineTo type.script.plus(binding.scriptLine)

val Binding.scriptLine: ScriptLine get() =
	isConstant.isConstantBindingName lineTo type.script

val Boolean.isConstantBindingName: String get() =
	if (this) "be" else "do"

val Context.scriptLine: ScriptLine get() =
	"context" lineTo script(
		dictionary.scriptLine,
		"params" lineTo paramExpressionStack.map { "param" lineTo this.script }.script)

val Compiler.scriptLine: ScriptLine get() =
	"compiler" lineTo script(
		context.scriptLine,
		"body" lineTo bodyTypedExpression.script)
