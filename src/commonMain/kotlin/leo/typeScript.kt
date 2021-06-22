package leo

import leo.base.runIf

val Type.scriptLine: ScriptLine get() =
	"type" lineTo script

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
		is FunctionTypeAtom -> function.scriptLine
		is PrimitiveTypeAtom -> primitive.scriptLine
	}

val TypePrimitive.scriptLine: ScriptLine get() =
	when (this) {
		is FieldTypePrimitive -> field.scriptLine
		is LiteralTypePrimitive -> literal.scriptLine
	}

val TypeField.scriptLine: ScriptLine get() =
	unescapedScriptLine.runIf(name.isTypeKeyword) { theName lineTo script(this) }

val TypeField.unescapedScriptLine: ScriptLine get() =
	name lineTo rhsType.script

val TypeLiteral.scriptLine: ScriptLine get() =
	when (this) {
		is NumberTypeLiteral -> numberTypeScriptLine
		is TextTypeLiteral -> textTypeScriptLine
	}

val TypeFunction.scriptLine: ScriptLine get() =
	functionName lineTo lhsType.script.plus(givesName lineTo rhsType.script)

val TypeRecursive.scriptLine: ScriptLine get() =
	recursiveName lineTo script(line.scriptLine)

@Suppress("unused")
val TypeRecurse.scriptLine: ScriptLine get() =
	recurseName lineTo script()

val String.isTypeKeyword: Boolean get() =
	when (this) {
		choiceName -> true
		functionName -> true
		numberName -> true
		recurseName -> true
		recursiveName -> true
		textName -> true
		theName -> true
		else -> false
	}