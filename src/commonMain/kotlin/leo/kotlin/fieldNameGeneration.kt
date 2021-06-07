package leo.kotlin

import leo.AtomTypeLine
import leo.DoingTypeAtom
import leo.FieldTypeAtom
import leo.ListTypeAtom
import leo.LiteralTypeAtom
import leo.NumberTypeLiteral
import leo.RecurseTypeLine
import leo.RecursiveTypeLine
import leo.TextTypeLiteral
import leo.TypeAtom
import leo.TypeLine
import leo.TypeLiteral

// TODO: Resolve duplicate field names

val TypeLine.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is AtomTypeLine -> atom.fieldNameGeneration
		is RecurseTypeLine -> TODO()
		is RecursiveTypeLine -> TODO()
	}

val TypeAtom.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is DoingTypeAtom -> "doing".generation
		is FieldTypeAtom -> field.name.nameKotlin.string.generation
		is ListTypeAtom -> "list".generation
		is LiteralTypeAtom -> literal.fieldNameGeneration
	}

val TypeLiteral.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is NumberTypeLiteral -> "number".generation
		is TextTypeLiteral -> "text".generation
	}
