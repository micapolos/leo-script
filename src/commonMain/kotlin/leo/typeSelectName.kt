package leo

val TypeLine.nameOrNullRecursion: TypeRecursion<String?>
  get() =
    atomRecursion.map { it.nameOrNull }

val TypeLine.nameOrNull: String?
  get() =
    nameOrNullRecursion.get(null)

val TypeAtom.nameOrNull: String?
  get() =
    when (this) {
      is FunctionTypeAtom -> functionName
      is PrimitiveTypeAtom -> primitive.nameOrNull
    }

val TypePrimitive.nameOrNull: String?
  get() =
    when (this) {
      is FieldTypePrimitive -> field.name
      is AnyTypePrimitive -> null
    }
