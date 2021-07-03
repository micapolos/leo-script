package leo

fun Value.matches(value: Value): Boolean =
	when (value) {
		anyValue -> true
		EmptyValue -> isEmpty
		is LinkValue -> matches(value.link)
	}

fun Value.matches(link: Link): Boolean =
	if (link.field.name == orName) link.field.rhs.valueOrNull?.let { matchesOr(link.value, it) } ?: false
	else linkOrNull?.matches(link)?:false

fun Value.matchesOr(lhs: Value, rhs: Value): Boolean =
	if (lhs.isEmpty) matches(rhs)
	else matches(lhs) || matches(rhs)

fun Link.matches(link: Link): Boolean =
	field.matches(link.field) && value.matches(link.value)

fun Field.matches(field: Field) =
	null
			?: matchesSpecial(field)
			?: matchesNamed(field)

fun Field.matchesNamed(field: Field) =
	name == field.name && rhs.matches(field.rhs)

fun Field.matchesSpecial(field: Field): Boolean? =
	when (field) {
		numberName fieldTo value() -> numberOrNull != null
		textName fieldTo value() -> textOrNull != null
		else -> null
	}

fun Rhs.matches(rhs: Rhs) =
	rhs.isAny || when (rhs) {
		is FunctionRhs -> (this is FunctionRhs) && this == rhs
		is NativeRhs -> (this is NativeRhs) && this == rhs
		is ValueRhs -> (this is ValueRhs) && value.matches(rhs.value)
	}

val Value.isAny get() = this == anyValue
val Rhs.isAny get() = (this is ValueRhs) && value.isAny

