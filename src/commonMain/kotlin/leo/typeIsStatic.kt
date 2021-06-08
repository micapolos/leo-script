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
		is RecursibleTypeLine -> recursible.isStatic
		is RecursiveTypeLine -> recursive.isStatic
	}

val TypeRecursible.isStatic: Boolean get() =
	when (this) {
		is AtomTypeRecursible -> atom.isStatic
		is RecurseTypeRecursible -> recurse.isStatic
	}

val TypeAtom.isStatic: Boolean get() =
	when (this) {
		is DoingTypeAtom -> doing.isStatic
		is FieldTypeAtom -> field.isStatic
		is LiteralTypeAtom -> literal.isStatic
	}

val TypeField.isStatic: Boolean get() = rhsType.isStatic
val TypeLiteral.isStatic: Boolean get() = false
val TypeRecurse.isStatic: Boolean get() = false
val TypeRecursive.isStatic: Boolean get() = false
val TypeDoing.isStatic: Boolean get() = false
