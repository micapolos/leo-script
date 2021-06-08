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
import leo.array
import leo.bind
import leo.flat
import leo.get
import leo.isTypeField
import leo.map

val TypeLine.typeName: String get() =
	typeNameGeneration.get(types())

val TypeLine.typeNameGeneration: Generation<String> get() =
	when (this) {
		is RecursibleTypeLine -> recursible.typeNameGeneration
		is RecursiveTypeLine -> TODO()
	}

val TypeRecursible.typeNameGeneration: Generation<String> get() =
	when (this) {
		is AtomTypeRecursible -> atom.typeNameGeneration
		is RecurseTypeRecursible -> TODO()
	}

val TypeAtom.typeNameGeneration: Generation<String> get() =
	when (this) {
		is DoingTypeAtom -> doing.typeNameGeneration
		is PrimitiveTypeAtom -> primitive.typeNameGeneration
	}

val TypePrimitive.typeNameGeneration: Generation<String> get() =
	when (this) {
		is FieldTypePrimitive -> field.typeNameGeneration
		is LiteralTypePrimitive -> literal.typeNameGeneration
	}


val TypeDoing.typeNameGeneration: Generation<String> get() =
	lhsTypeStructure.lineStack
		.map { typeNameGeneration }
		.flat
		.bind { lhsKotlinTypeStack ->
			rhsTypeLine.typeNameGeneration.bind { rhsTypeName ->
				"(${lhsKotlinTypeStack.array.joinToString(", ")}) -> $rhsTypeName".generation
			}
		}

val TypeLiteral.typeNameGeneration: Generation<String> get() =
	when (this) {
		is NumberTypeLiteral -> "Double".generation
		is TextTypeLiteral -> "String".generation
	}

val TypeField.typeNameGeneration: Generation<String> get() =
	if (this == isTypeField) "Boolean".generation
	else nameGeneration.map { it.kotlinClassName }
