package leo.named.compiler

import leo.ScriptLine
import leo.lineTo
import leo.map
import leo.named.expression.scriptLine
import leo.named.typed.scriptLine
import leo.plus
import leo.script

val Dictionary.scriptLine: ScriptLine get() =
	"definitions" lineTo definitionStack.map { scriptLine }.script

val Definition.scriptLine: ScriptLine get() =
	"definition" lineTo type.script.plus(binding.scriptLine)

val Binding.scriptLine: ScriptLine get() =
	isConstant.isConstantBindingName lineTo type.script

val Boolean.isConstantBindingName: String get() =
	if (this) "is" else "does"

val Context.scriptLine: ScriptLine get() =
	"context" lineTo script(dictionary.scriptLine, scope.scriptLine)

val Compiler.scriptLine: ScriptLine get() =
	"compiler" lineTo script(context.scriptLine, bodyTypedExpression.scriptLine)

val Scope.scriptLine: ScriptLine get() =
	"bindings" lineTo bindingStack.map { scriptLine }.script

val Module.scriptLine: ScriptLine get() =
	"module" lineTo script(
		"private" lineTo script(privateDictionary.scriptLine),
		"public" lineTo script(publicDictionary.scriptLine))
