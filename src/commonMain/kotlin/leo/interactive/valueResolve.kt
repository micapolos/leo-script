package leo.interactive

import leo.Field
import leo.Value
import leo.base.fold
import leo.base.orNull
import leo.field
import leo.fieldSeq
import leo.isEmpty
import leo.literal
import leo.nameOrNull
import leo.numberName
import leo.rhsOrNull
import leo.valueOrNull

val Field.resolveOrNull: Field? get() =
	when (name) {
		numberName -> rhs.valueOrNull?.doubleResolveOrNull?.let { field(literal(it)) }
		else -> this
	}

val Value.doubleResolveOrNull: Double? get() =
	if (isEmpty) null
	else 0.0.orNull.fold(fieldSeq) { field ->
		field.digitOrNull?.let { digit -> this?.times(10)?.plus(digit) }
	}

val Field.digitOrNull: Int? get() =
	rhsOrNull(name)?.valueOrNull?.nameOrNull?.digitOrNull

val String.digitOrNull: Int? get() =
	when (this) {
		"zero" -> 0
		"one" -> 1
		"two" -> 2
		"three" -> 3
		"four" -> 4
		"five" -> 5
		"six" -> 6
		"seven" -> 7
		"eight" -> 8
		"nine" -> 9
		else -> null
	}
