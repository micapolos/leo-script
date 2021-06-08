package leo

val Type.normalizeRecursion: Type get() =
	when (this) {
		is ChoiceType -> choice.normalizeRecursion.type
		is StructureType -> structure.normalizeRecursion.type
	}

val TypeStructure.normalizeRecursion: TypeStructure get() =
	lineStack.normalizeRecursion.structure

val TypeChoice.normalizeRecursion: TypeChoice get() =
	lineStack.normalizeRecursion.choice

val Stack<TypeLine>.normalizeRecursion: Stack<TypeLine> get() =
	map { normalizeRecursion }

val TypeLine.normalizeRecursion: TypeLine get() =
	when (this) {
		is RecursibleTypeLine -> recursible.normalizeRecursion.line
		is RecursiveTypeLine -> recursive.normalizeRecursion.toLine
	}.unshiftRecursion

val TypeRecursible.normalizeRecursion: TypeRecursible get() =
	when (this) {
		is AtomTypeRecursible -> atom.normalizeRecursion.recursible
		is RecurseTypeRecursible -> recurse.normalizeRecursion.recursible
	}

val TypeRecurse.normalizeRecursion: TypeRecurse get() = this

val TypeRecursive.normalizeRecursion: TypeRecursive get() =
	line.normalizeRecursion.recursive

val TypeAtom.normalizeRecursion: TypeAtom get() =
	when (this) {
		is DoingTypeAtom -> doing.normalizeRecursion.atom
		is PrimitiveTypeAtom -> primitive.normalizeRecursion.atom
	}

val TypeDoing.normalizeRecursion: TypeDoing get() =
	lhsTypeStructure.normalizeRecursion doing rhsTypeLine.normalizeRecursion

val TypePrimitive.normalizeRecursion: TypePrimitive get() =
	when (this) {
		is FieldTypePrimitive -> field.normalizeRecursion.primitive
		is LiteralTypePrimitive -> literal.normalizeRecursion.primitive
	}

val TypeField.normalizeRecursion: TypeField get() =
	name fieldTo rhsType.normalizeRecursion

val TypeLiteral.normalizeRecursion: TypeLiteral get() = this