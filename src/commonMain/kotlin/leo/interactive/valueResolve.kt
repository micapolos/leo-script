package leo.interactive

import leo.Field
import leo.Value
import leo.base.fold
import leo.base.orNull
import leo.contentName
import leo.contentOrNull
import leo.field
import leo.fieldSeq
import leo.getOrNull
import leo.isEmpty
import leo.literal
import leo.nameOrNull
import leo.numberName
import leo.onlyFieldOrNull
import leo.rhsOrNull
import leo.valueOrNull

val Field.resolveOrNull: Field? get() =
	when (name) {
		numberName -> rhs.valueOrNull?.doubleResolveOrNull?.let { field(literal(it)) }
		else -> this
	}

val Value.resolve: Value get() =
	resolveOrNull ?: this

val Value.resolveOrNull: Value? get() =
	null
		?: onlyFieldOrNull?.resolveValueOrNull

val Field.resolveValueOrNull: Value? get() =
	null
		?: resolveGetOrNull
		?: resolveCommandOrNull

val Field.resolveGetOrNull: Value? get() =
	rhs.valueOrNull?.getOrNull(name)

val Field.resolveCommandOrNull: Value? get() =
	when (name) {
		contentName -> rhs.valueOrNull?.contentOrNull
		else -> null
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
