package leo

val Type.script: Script get() =
	when (this) {
		is StructureType -> structure.script
		is ChoiceType -> choice.script
	}

val TypeStructure.script: Script get() =
	lineStack.map { scriptLine }.script

val TypeChoice.script: Script get() =
	script(choiceName lineTo lineStack.map { scriptLine }.script)

val TypeLine.scriptLine: ScriptLine get() =
	when (this) {
		is AtomTypeLine -> atom.scriptLine
		is RecursiveTypeLine -> recursive.scriptLine
		is RecurseTypeLine -> recurse.scriptLine
	}

val TypeAtom.scriptLine: ScriptLine get() =
	when (this) {
		is DoingTypeAtom -> doing.scriptLine
		is FieldTypeAtom -> field.scriptLine
		is ListTypeAtom -> list.scriptLine
		is LiteralTypeAtom -> literal.scriptLine
	}

val TypeField.scriptLine: ScriptLine get() =
	name lineTo type.script

val TypeList.scriptLine: ScriptLine get() =
	listName lineTo script(itemLine.scriptLine)

val TypeLiteral.scriptLine: ScriptLine get() =
	when (this) {
		is NumberTypeLiteral -> numberName lineTo script()
		is TextTypeLiteral -> textName lineTo script()
	}

val TypeDoing.scriptLine: ScriptLine get() =
	doingName lineTo lhsTypeStructure.script.plus(toName lineTo script(rhsTypeLine.scriptLine))

val TypeRecursive.scriptLine: ScriptLine get() =
	recursiveName lineTo script(line.scriptLine)

@Suppress("unused")
val TypeRecurse.scriptLine: ScriptLine get() =
	recurseName lineTo script()