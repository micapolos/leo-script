package leo.kotlin

import leo.AtomTypeRecursible
import leo.DoingTypeAtom
import leo.FieldTypeAtom
import leo.LiteralTypeAtom
import leo.NumberTypeLiteral
import leo.RecurseTypeRecursible
import leo.RecursibleTypeLine
import leo.RecursiveTypeLine
import leo.TextTypeLiteral
import leo.TypeAtom
import leo.TypeLine
import leo.TypeLiteral
import leo.TypeRecursible

// TODO: Resolve duplicate field names

val TypeLine.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is RecursibleTypeLine -> recursible.fieldNameGeneration
		is RecursiveTypeLine -> TODO()
	}

val TypeRecursible.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is AtomTypeRecursible -> atom.fieldNameGeneration
		is RecurseTypeRecursible -> TODO()
	}

val TypeAtom.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is DoingTypeAtom -> "doing".generation
		is FieldTypeAtom -> field.name.nameKotlin.string.generation
		is LiteralTypeAtom -> literal.fieldNameGeneration
	}

val TypeLiteral.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is NumberTypeLiteral -> "number".generation
		is TextTypeLiteral -> "text".generation
	}
