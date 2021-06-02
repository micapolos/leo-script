package leo

val Type.normalize: Type get() =
	when (this) {
		AnyType -> this
		EmptyType -> this
		is LinkType -> type(link.normalize)
	}

val TypeLink.normalize: TypeLink get() =
	if (rhsField.shouldNormalize) emptyType linkTo (rhsField.name fieldTo lhsType.normalize)
	else lhsType linkTo rhsField.normalize

val TypeField.normalize: TypeField get() =
	name fieldTo rhs.normalize

val TypeField.shouldNormalize: Boolean get() =
	rhs is TypeTypeRhs && rhs.type is EmptyType

val TypeRhs.normalize: TypeRhs get() =
	when (this) {
		is NativeTypeRhs -> this
		is TypeTypeRhs -> rhs(type.normalize)
	}