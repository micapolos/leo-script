package leo

val Value.scriptLine: ScriptLine get() =
	"value" lineTo script

val Evaluator.scriptLine: ScriptLine get() =
	"evaluator" lineTo script(
		context.scriptLine,
		"value" lineTo value.script)

val Context.scriptLine: ScriptLine get() =
	"context" lineTo script(
		"public" lineTo script(publicDictionary.scriptLine),
		"private" lineTo script(privateDictionary.scriptLine))

val Dictionary.scriptLine: ScriptLine get() =
	"dictionary" lineTo script(
		listName lineTo script(definitionStack.map { scriptLine }))

val Definition.scriptLine: ScriptLine get() =
	"definition" lineTo script(
		when (this) {
			is LetDefinition -> let.scriptLine
			is RecursiveDefinition -> recursive.scriptLine
		}
	)

val DefinitionLet.scriptLine: ScriptLine get() =
	"let" lineTo script(
		"pattern" lineTo value.script,
		binding.scriptLine)

val Binding.scriptLine: ScriptLine get() =
	"binding" lineTo script(
		when (this) {
			is BinderBinding -> binder.scriptLine
			is RecurseBinding -> recurse.scriptLine
			is ValueBinding -> value.scriptLine
		}
	)

val Body.scriptLine: ScriptLine get() =
	"body" lineTo script(
		when (this) {
			is BlockBody -> block.scriptLine
			is FnBody -> "native" lineTo script("function")
		}
	)

val Block.scriptLine: ScriptLine get() =
	"block" lineTo
		when (this) {
			is RecursingBlock -> script(recursing.scriptLine)
			is SyntaxBlock -> syntax.script
		}

val DictionaryRecursive.scriptLine: ScriptLine get() = "recursive" lineTo script(dictionary.scriptLine)
val Recursing.scriptLine: ScriptLine get() = "recursing" lineTo syntax.script
val BodyRecurse.scriptLine: ScriptLine get() = "recurse" lineTo script(body.scriptLine)