package leo

import leo.base.notNullIf

val TypeLine.atom: TypeAtom get() =
	when (this) {
		is AtomTypeLine -> atom
		is RecurseTypeLine -> error("$this.shiftRecursion")
		is RecursiveTypeLine -> recursive.atom.shiftRecursion
	}

val TypeAtom.shiftRecursion: TypeAtom get() =
	when (this) {
		is DoingTypeAtom -> this
		is FieldTypeAtom -> atom(field.shiftRecursion)
		is LiteralTypeAtom -> this
	}

val TypeField.shiftRecursion: TypeField get() =
	name fieldTo rhsType.shiftRecursionWithName(name)

fun Type.shiftRecursionWithName(name: String): Type =
	when (this) {
		is ChoiceType -> choice.shiftRecursionWithName(name).type
		is StructureType -> structure.shiftRecursionWithName(name).type
	}

fun TypeStructure.shiftRecursionWithName(name: String): TypeStructure =
	lineStack.typeLineShiftRecursionWithName(name).structure

fun TypeChoice.shiftRecursionWithName(name: String): TypeChoice =
	lineStack.typeLineShiftRecursionWithName(name).choice

fun Stack<TypeLine>.typeLineShiftRecursionWithName(name: String): Stack<TypeLine> =
	mapRope { rope ->
		rope.current.updateRecurseWith(
			name lineTo rope
				.updateCurrent { line(recursive(it.atomOrNull!!)) }
				.stack.structure.type)
	}

// =====================================================

fun TypeLine.updateRecurseWith(line: TypeLine): TypeLine =
	updateRecurseOrNullWith(line) ?: this

fun Type.updateRecurseOrNullWith(line: TypeLine): Type? =
	when (this) {
		is ChoiceType -> choice.updateRecurseOrNullWith(line)?.let(::type)
		is StructureType -> structure.updateRecurseOrNullWith(line)?.let(::type)
	}

fun TypeChoice.updateRecurseOrNullWith(line: TypeLine): TypeChoice? =
	notNullIf(lineStack.any { updateRecurseOrNullWith(line) != null }) {
		lineStack.map { updateRecurseOrNullWith(line) ?: this }.choice
	}

fun TypeStructure.updateRecurseOrNullWith(line: TypeLine): TypeStructure? =
	notNullIf(lineStack.any { updateRecurseOrNullWith(line) != null }) {
		lineStack.map { updateRecurseOrNullWith(line) ?: this }.structure
	}

fun TypeLine.updateRecurseOrNullWith(line: TypeLine): TypeLine? =
	when (this) {
		is AtomTypeLine -> atom.updateRecurseOrNullWith(line)?.let(::line)
		is RecurseTypeLine -> line
		is RecursiveTypeLine -> null
	}

fun TypeAtom.updateRecurseOrNullWith(line: TypeLine): TypeAtom? =
	when (this) {
		is DoingTypeAtom -> null
		is FieldTypeAtom -> field.updateRecurseOrNullWith(line)?.let(::atom)
		is LiteralTypeAtom -> null
	}

fun TypeField.updateRecurseOrNullWith(line: TypeLine): TypeField? =
	rhsType.updateRecurseOrNullWith(line)?.let { type ->
		name fieldTo type
	}
