package leo.named.value

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral
import leo.doingName
import leo.nativeName
import leo.numberName
import leo.textName

val <T> ValueLine<T>.name get() =
	when (this) {
		is AnyValueLine -> nativeName
		is FieldValueLine -> field.name
		is FunctionValueLine -> doingName
		is LiteralValueLine -> literal.name
	}

val Literal.name: String get() =
	when (this) {
		is NumberLiteral -> numberName
		is StringLiteral -> textName
	}