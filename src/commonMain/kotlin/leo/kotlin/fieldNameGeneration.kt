package leo.kotlin

import leo.AtomTypeRecursible
import leo.DoingTypeAtom
import leo.FieldTypePrimitive
import leo.LiteralTypePrimitive
import leo.NumberTypeLiteral
import leo.PrimitiveTypeAtom
import leo.RecurseTypeRecursible
import leo.RecursibleTypeLine
import leo.RecursiveTypeLine
import leo.TextTypeLiteral
import leo.TypeAtom
import leo.TypeDoing
import leo.TypeField
import leo.TypeLine
import leo.TypeLiteral
import leo.TypePrimitive
import leo.TypeRecursible
import leo.doingName

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
		is DoingTypeAtom -> doing.fieldNameGeneration
		is PrimitiveTypeAtom -> primitive.fieldNameGeneration
	}

val TypeField.fieldNameGeneration: Generation<String> get() =
	name.nameKotlin.string.generation

val TypePrimitive.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is FieldTypePrimitive -> field.fieldNameGeneration
		is LiteralTypePrimitive -> literal.fieldNameGeneration
	}

val TypeLiteral.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is NumberTypeLiteral -> "number".generation
		is TextTypeLiteral -> "text".generation
	}

@Suppress("unused")
val TypeDoing.fieldNameGeneration: Generation<String> get() =
	doingName.generation