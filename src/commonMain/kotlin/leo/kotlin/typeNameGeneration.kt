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
import leo.TypeDoing
import leo.TypeField
import leo.TypeLine
import leo.TypeList
import leo.TypeLiteral
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
		is AtomTypeLine -> atom.typeNameGeneration
		is RecurseTypeLine -> TODO()
		is RecursiveTypeLine -> TODO()
	}

val TypeAtom.typeNameGeneration: Generation<String> get() =
	when (this) {
		is DoingTypeAtom -> doing.typeNameGeneration
		is FieldTypeAtom -> field.typeNameGeneration
		is ListTypeAtom -> list.typeNameGeneration
		is LiteralTypeAtom -> literal.typeNameGeneration
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

val TypeList.typeNameGeneration: Generation<String> get() =
	itemLine.typeNameGeneration.bind { itemKotlinType ->
		"Stack<${itemKotlinType}>".generation
	}

val TypeLiteral.typeNameGeneration: Generation<String> get() =
	when (this) {
		is NumberTypeLiteral -> "Double".generation
		is TextTypeLiteral -> "String".generation
	}

val TypeField.typeNameGeneration: Generation<String> get() =
	if (this == isTypeField) "Boolean".generation
	else nameGeneration.map { it.kotlinClassName }
