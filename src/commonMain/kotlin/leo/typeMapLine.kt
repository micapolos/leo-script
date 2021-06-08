package leo

fun Type.mapLine(fn: (TypeLine) -> TypeLine): Type =
	when (this) {
		is ChoiceType -> choice.mapLine(fn).type
		is StructureType -> structure.mapLine(fn).type
	}

fun TypeStructure.mapLine(fn: (TypeLine) -> TypeLine): TypeStructure  =
	lineStack.mapLine(fn).structure

fun TypeChoice.mapLine(fn: (TypeLine) -> TypeLine): TypeChoice  =
	lineStack.mapLine(fn).choice

fun Stack<TypeLine>.mapLine(fn: (TypeLine) -> TypeLine): Stack<TypeLine>  =
	map { mapLine(fn) }

fun TypeLine.mapLine(fn: (TypeLine) -> TypeLine): TypeLine  =
	fn(when (this) {
		is RecursibleTypeLine -> recursible.mapLine(fn).line
		is RecursiveTypeLine -> recursive.mapLine(fn).toLine
	})

fun TypeRecursible.mapLine(fn: (TypeLine) -> TypeLine): TypeRecursible  =
	when (this) {
		is AtomTypeRecursible -> atom.mapLine(fn).recursible
		is RecurseTypeRecursible -> recurse.mapLine(fn).recursible
	}

fun TypeRecurse.mapLine(fn: (TypeLine) -> TypeLine): TypeRecurse  = this

fun TypeRecursive.mapLine(fn: (TypeLine) -> TypeLine): TypeRecursive  =
	line.mapLine(fn).recursive

fun TypeAtom.mapLine(fn: (TypeLine) -> TypeLine): TypeAtom  =
	when (this) {
		is DoingTypeAtom -> doing.mapLine(fn).atom
		is PrimitiveTypeAtom -> primitive.mapLine(fn).atom
	}

fun TypeDoing.mapLine(fn: (TypeLine) -> TypeLine): TypeDoing  =
	lhsTypeStructure.mapLine(fn) doing rhsTypeLine.mapLine(fn)

fun TypePrimitive.mapLine(fn: (TypeLine) -> TypeLine): TypePrimitive  =
	when (this) {
		is FieldTypePrimitive -> field.mapLine(fn).primitive
		is LiteralTypePrimitive -> literal.mapLine(fn).primitive
	}

fun TypeField.mapLine(fn: (TypeLine) -> TypeLine): TypeField  =
	name fieldTo rhsType.mapLine(fn)

fun TypeLiteral.mapLine(fn: (TypeLine) -> TypeLine): TypeLiteral  = this