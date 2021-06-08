package leo

val TypeLine.nameRecursion: TypeRecursion<String> get() =
	atomRecursion.map { it.name }

val TypeLine.name: String get() =
	nameRecursion.get(null)

val TypeAtom.name: String get() =
	when (this) {
		is DoingTypeAtom -> doingName
		is PrimitiveTypeAtom -> primitive.name
	}

val TypePrimitive.name: String get() =
	when (this) {
		is FieldTypePrimitive -> field.name
		is LiteralTypePrimitive -> literal.name
	}

val TypeLiteral.name: String get() =
	when (this) {
		is NumberTypeLiteral -> numberName
		is TextTypeLiteral -> textName
	}

