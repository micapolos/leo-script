package leo

fun Type.updateLine(fn: (TypeLine) -> TypeLine): Type =
	when (this) {
		is ChoiceType -> choice.updateLine(fn).type
		is StructureType -> structure.updateLine(fn).type
	}

fun TypeStructure.updateLine(fn: (TypeLine) -> TypeLine): TypeStructure  =
	lineStack.updateLine(fn).structure

fun TypeChoice.updateLine(fn: (TypeLine) -> TypeLine): TypeChoice  =
	lineStack.updateLine(fn).choice

fun Stack<TypeLine>.updateLine(fn: (TypeLine) -> TypeLine): Stack<TypeLine>  =
	map { updateLine(fn) }

fun TypeLine.updateLine(fn: (TypeLine) -> TypeLine): TypeLine  =
	fn(when (this) {
		is RecursibleTypeLine -> recursible.updateLine(fn).line
		is RecursiveTypeLine -> recursive.updateLine(fn).toLine
	})

fun TypeRecursible.updateLine(fn: (TypeLine) -> TypeLine): TypeRecursible  =
	when (this) {
		is AtomTypeRecursible -> atom.updateLine(fn).recursible
		is RecurseTypeRecursible -> recurse.updateLine(fn).recursible
	}

fun TypeRecurse.updateLine(@Suppress("UNUSED_PARAMETER") fn: (TypeLine) -> TypeLine): TypeRecurse  = this

fun TypeRecursive.updateLine(fn: (TypeLine) -> TypeLine): TypeRecursive  =
	line.updateLine(fn).recursive

fun TypeAtom.updateLine(fn: (TypeLine) -> TypeLine): TypeAtom  =
	when (this) {
		is DoingTypeAtom -> doing.updateLine(fn).atom
		is PrimitiveTypeAtom -> primitive.updateLine(fn).atom
	}

fun TypeDoing.updateLine(fn: (TypeLine) -> TypeLine): TypeDoing  =
	lhsType.updateLine(fn) doing rhsTypeLine.updateLine(fn)

fun TypePrimitive.updateLine(fn: (TypeLine) -> TypeLine): TypePrimitive  =
	when (this) {
		is FieldTypePrimitive -> field.updateLine(fn).primitive
		is LiteralTypePrimitive -> literal.updateLine(fn).primitive
	}

fun TypeField.updateLine(fn: (TypeLine) -> TypeLine): TypeField  =
	name fieldTo rhsType.updateLine(fn)

fun TypeLiteral.updateLine(@Suppress("UNUSED_PARAMETER") fn: (TypeLine) -> TypeLine): TypeLiteral  = this