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
		is RecursiveTypeLine -> recursive.scriptLine
		is RecursibleTypeLine -> recursible.scriptLine
	}

val TypeRecursible.scriptLine: ScriptLine get() =
	when (this) {
		is AtomTypeRecursible -> atom.scriptLine
		is RecurseTypeRecursible -> recurse.scriptLine
	}

val TypeAtom.scriptLine: ScriptLine get() =
	when (this) {
		is DoingTypeAtom -> doing.scriptLine
		is PrimitiveTypeAtom -> primitive.scriptLine
	}

val TypePrimitive.scriptLine: ScriptLine get() =
	when (this) {
		is FieldTypePrimitive -> field.scriptLine
		is LiteralTypePrimitive -> literal.scriptLine
	}

val TypeField.scriptLine: ScriptLine get() =
	name lineTo rhsType.script

val TypeLiteral.scriptLine: ScriptLine get() =
	when (this) {
		is NumberTypeLiteral -> numberName lineTo script()
		is TextTypeLiteral -> textName lineTo script()
	}

val TypeDoing.scriptLine: ScriptLine get() =
	doingName lineTo lhsType.script.plus(toName lineTo script(rhsTypeLine.scriptLine))

val TypeRecursive.scriptLine: ScriptLine get() =
	recursiveName lineTo script(line.scriptLine)

@Suppress("unused")
val TypeRecurse.scriptLine: ScriptLine get() =
	recurseName lineTo script()
