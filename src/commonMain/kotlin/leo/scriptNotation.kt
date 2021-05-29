package leo

val Script.notation: Notation
	get() =
		when (this) {
			is LinkScript -> notation(link.notationLink)
			is UnitScript -> EmptyNotation
		}

val ScriptLink.notationLink: NotationLink
	get() =
		when (lhs) {
			is LinkScript -> lhs.link.notationLink.plus(line)
			is UnitScript -> emptyNotation linkTo line.notationLine
		}

fun NotationLink.plus(scriptLine: ScriptLine): NotationLink =
	when (scriptLine) {
		is FieldScriptLine -> plus(scriptLine.field)
		is LiteralScriptLine -> plus(scriptLine.literal)
	}

fun NotationLink.plus(scriptField: ScriptField): NotationLink =
	when (scriptField.rhs) {
		is UnitScript -> plus(scriptField.string)
		is LinkScript -> notation(this) linkTo scriptField.notationLine
	}

val ScriptField.notationLine: NotationLine
	get() =
		when (rhs) {
			is UnitScript -> line(chain(atom(string)))
			is LinkScript -> line(string fieldTo rhs.link.notationLink)
		}

val ScriptLine.notationLine: NotationLine
	get() =
		when (this) {
			is FieldScriptLine -> field.notationLine
			is LiteralScriptLine -> line(chain(atom(literal)))
		}