package leo

// TODO: Create custom implementation parsed from Script
data class Pattern(val script: Script)

fun pattern(script: Script) = Pattern(script)

fun Value.isMatching(pattern: Pattern) =
	isMatching(pattern.script)

fun Value.isMatching(script: Script): Boolean =
	if (script == script(anyName)) true
	else isMatchingLines(script)

fun Value.isMatchingLines(script: Script): Boolean =
	when (script) {
		is UnitScript -> isEmpty
		is LinkScript -> linkOrNull?.isMatching(script.link)?:false
	}

fun Link.isMatching(scriptLink: ScriptLink): Boolean =
	field.isMatching(scriptLink.line) && value.isMatching(scriptLink.lhs)

fun Field.isMatching(scriptLine: ScriptLine): Boolean =
	when (scriptLine) {
		is FieldScriptLine -> isMatching(scriptLine.field)
		is LiteralScriptLine -> isMatching(scriptLine.literal)
	}

fun Field.isMatching(scriptField: ScriptField): Boolean =
	name == scriptField.string && rhs.isMatching(scriptField.rhs)

fun Rhs.isMatching(script: Script): Boolean =
	when (this) {
		is FunctionRhs -> script == script(anyName)
		is NativeRhs -> script == script(anyName)
		is PatternRhs -> script == script(anyName)
		is ValueRhs -> value.isMatching(script)
	}

fun Field.isMatching(literal: Literal): Boolean =
	when (literal) {
		is NumberLiteral -> numberOrNull?.equals(literal.number)?:false
		is StringLiteral -> textOrNull?.equals(literal.string)?:false
	}
