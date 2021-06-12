package leo.named.value

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral
import leo.doingName
import leo.nativeName
import leo.numberName
import leo.textName

val <T> Value<T>.name get() =
	when (this) {
		is AnyValue -> nativeName
		is FieldValue -> field.name
		is FunctionValue -> doingName
		is LiteralValue -> literal.name
	}

val Literal.name: String get() =
	when (this) {
		is NumberLiteral -> numberName
		is StringLiteral -> textName
	}