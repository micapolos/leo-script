package leo.kotlin

import leo.AtomTypeLine
import leo.ChoiceType
import leo.DoingTypeAtom
import leo.FieldTypeAtom
import leo.ListTypeAtom
import leo.LiteralTypeAtom
import leo.RecurseTypeLine
import leo.RecursiveTypeLine
import leo.StructureType
import leo.Type
import leo.TypeAtom
import leo.TypeChoice
import leo.TypeField
import leo.TypeLine
import leo.TypeStructure

val Type.funGeneration: Generation<String> get() =
	when (this) {
		is ChoiceType -> TODO()
		is StructureType -> TODO()
	}

val TypeStructure.funGeneration: Generation<String> get() =
	TODO()

val TypeChoice.funGeneration: Generation<String> get() =
	TODO()

val TypeLine.funGeneration: Generation<String> get() =
	when (this) {
		is AtomTypeLine -> atom.funGeneration
		is RecurseTypeLine -> TODO()
		is RecursiveTypeLine -> TODO()
	}

val TypeAtom.funGeneration: Generation<String> get() =
	when (this) {
		is DoingTypeAtom -> TODO()
		is FieldTypeAtom -> TODO()
		is ListTypeAtom -> TODO()
		is LiteralTypeAtom -> TODO()
	}

val TypeField.funGeneration: Generation<String> get() =
	TODO()
