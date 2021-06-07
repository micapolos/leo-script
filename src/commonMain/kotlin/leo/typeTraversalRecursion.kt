package leo

import leo.base.notNullIf

fun Type.updateRecurseWith(line: TypeLine): Type? =
	when (this) {
		is ChoiceType -> choice.updateRecurseWith(line)?.let(::type)
		is StructureType -> structure.updateRecurseWith(line)?.let(::type)
	}

fun TypeChoice.updateRecurseWith(line: TypeLine): TypeChoice? =
	notNullIf(lineStack.any { updateRecurseWith(line) != null }) {
		lineStack.map { updateRecurseWith(line) ?: this }.choice
	}

fun TypeStructure.updateRecurseWith(line: TypeLine): TypeStructure? =
	notNullIf(lineStack.any { updateRecurseWith(line) != null }) {
		lineStack.map { updateRecurseWith(line) ?: this }.structure
	}

fun TypeLine.updateRecurseWith(line: TypeLine): TypeLine? =
	when (this) {
		is AtomTypeLine -> atom.updateRecurseWith(line)?.let(::line)
		is RecurseTypeLine -> line
		is RecursiveTypeLine -> null
	}

fun TypeAtom.updateRecurseWith(line: TypeLine): TypeAtom? =
	when (this) {
		is DoingTypeAtom -> null
		is FieldTypeAtom -> field.updateRecurseWith(line)?.let(::atom)
		is ListTypeAtom -> list.updateRecurseWith(line)?.let(::atom)
		is LiteralTypeAtom -> null
	}

fun TypeField.updateRecurseWith(line: TypeLine): TypeField? =
	rhsType.updateRecurseWith(line)?.let { type ->
		name fieldTo type
	}

fun TypeList.updateRecurseWith(line: TypeLine): TypeList? =
	itemLine.updateRecurseWith(line)?.let { list(it) }
