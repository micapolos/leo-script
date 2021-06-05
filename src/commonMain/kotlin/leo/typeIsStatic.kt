package leo

val Type.isStatic: Boolean get() =
	when (this) {
		is ChoiceType -> choice.isStatic
		is StructureType -> structure.isStatic
	}

val TypeStructure.isStatic: Boolean get() = lineStack.all { isStatic }
val TypeChoice.isStatic: Boolean get() = false

val TypeLine.isStatic: Boolean get() =
	when (this) {
		is AtomTypeLine -> atom.isStatic
		is RecurseTypeLine -> recurse.isStatic
		is RecursiveTypeLine -> recursive.isStatic
	}

val TypeAtom.isStatic: Boolean get() =
	when (this) {
		is DoingTypeAtom -> doing.isStatic
		is FieldTypeAtom -> field.isStatic
		is ListTypeAtom -> list.isStatic
		is LiteralTypeAtom -> literal.isStatic
	}

val TypeField.isStatic: Boolean get() = type.isStatic
val TypeList.isStatic: Boolean get() = false
val TypeLiteral.isStatic: Boolean get() = false
val TypeRecurse.isStatic: Boolean get() = false
val TypeRecursive.isStatic: Boolean get() = false
val TypeDoing.isStatic: Boolean get() = false
