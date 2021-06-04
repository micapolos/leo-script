package leo

fun Value.matches(value: Value): Boolean =
	when (value) {
		anyValue -> true
		EmptyValue -> isEmpty
		is LinkValue -> linkOrNull?.matches(value.link)?:false
	}

fun Link.matches(link: Link): Boolean =
	field.matches(link.field) && value.matches(link.value)

fun Field.matches(field: Field) =
	name == field.name && rhs.matches(field.rhs)

fun Rhs.matches(rhs: Rhs) =
	rhs.isAny || when (rhs) {
		is FunctionRhs -> (this is FunctionRhs) && this == rhs
		is NativeRhs -> (this is NativeRhs) && this == rhs
		is ValueRhs -> (this is ValueRhs) && value.matches(rhs.value)
	}

val Value.isAny get() = this == anyValue
val Rhs.isAny get() = (this is ValueRhs) && value.isAny

