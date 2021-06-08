package leo

import leo.base.notNullIf

val TypeLine.atom: TypeAtom get() =
	when (this) {
		is AtomTypeLine -> atom
		is RecurseTypeLine -> error("$this.shiftRecursion")
		is RecursiveTypeLine -> recursive.line.shiftRecursion.atom
	}

val TypeLine.shiftRecursion: TypeLine get() =
	when (this) {
		is AtomTypeLine -> atom.shiftRecursion.line
		is RecurseTypeLine -> error("$this.shiftRecursion")
		is RecursiveTypeLine -> this
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
		rope.current
			.replaceNonRecursiveOrNull(
				line(typeRecurse),
				name lineTo rope
					.updateCurrent { line(typeRecurse) }
					.stack.structure.type)
			?.let { line(recursive(it)) }
			?:rope.current
	}

// =====================================================

fun Type.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): Type? =
	when (this) {
		is ChoiceType -> choice.replaceNonRecursiveOrNull(line, newLine)?.type
		is StructureType -> structure.replaceNonRecursiveOrNull(line, newLine)?.type
	}

fun TypeChoice.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeChoice? =
	lineStack.replaceNonRecursiveOrNull(line, newLine)?.choice

fun TypeStructure.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeStructure? =
	lineStack.replaceNonRecursiveOrNull(line, newLine)?.structure

fun Stack<TypeLine>.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): Stack<TypeLine>? =
	notNullIf(any { replaceNonRecursiveOrNull(line, newLine) != null }) {
		map { replaceNonRecursiveOrNull(line, newLine) ?: this}
	}

fun TypeLine.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeLine? =
	if (this == line) newLine
	else when (this) {
		is AtomTypeLine -> atom.replaceNonRecursiveOrNull(line, newLine)?.line
		is RecurseTypeLine -> null
		is RecursiveTypeLine -> null
	}

fun TypeAtom.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeAtom? =
	when (this) {
		is DoingTypeAtom -> null
		is FieldTypeAtom -> field.replaceNonRecursiveOrNull(line, newLine)?.atom
		is LiteralTypeAtom -> null
	}

fun TypeField.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeField? =
	rhsType.replaceNonRecursiveOrNull(line, newLine)?.let { name fieldTo it }
