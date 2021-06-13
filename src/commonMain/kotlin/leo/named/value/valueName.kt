package leo.named.value

import leo.doingName
import leo.nativeName
import leo.numberName
import leo.textName

val ValueLine.name get() =
	when (this) {
		is AnyValueLine -> any.valueName
		is FieldValueLine -> field.name
		is FunctionValueLine -> doingName
	}

val Any?.valueName: String get() =
	when (this) {
		is String -> textName
		is Double -> numberName
		else -> nativeName
	}
