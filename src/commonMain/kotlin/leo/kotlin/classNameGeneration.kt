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
import leo.TypeLine
import leo.TypeLiteral
import leo.TypePrimitive
import leo.TypeRecursible
import leo.base.titleCase
import leo.doingName
import leo.get
import leo.numberName
import leo.textName

val TypeLine.className: String get() =
	classNameGeneration.get(types())

val TypeLine.classNameGeneration: Generation<String> get() =
	when (this) {
		is RecursibleTypeLine -> recursible.classNameGeneration
		is RecursiveTypeLine -> TODO()
	}

val TypeRecursible.classNameGeneration: Generation<String> get() =
	when (this) {
		is AtomTypeRecursible -> atom.classNameGeneration
		is RecurseTypeRecursible -> TODO()
	}

val TypeAtom.classNameGeneration: Generation<String> get() =
	when (this) {
		is DoingTypeAtom -> doing.classNameGeneration
		is PrimitiveTypeAtom -> primitive.classNameGeneration
	}

val TypePrimitive.classNameGeneration: Generation<String> get() =
	when (this) {
		is FieldTypePrimitive -> field.typeNameGeneration
		is LiteralTypePrimitive -> literal.classNameGeneration
	}

val TypeLiteral.classNameGeneration: Generation<String> get() =
	when (this) {
		is NumberTypeLiteral -> numberName.titleCase.generation
		is TextTypeLiteral -> textName.titleCase.generation
	}

val TypeDoing.classNameGeneration: Generation<String> get() =
	doingName.titleCase.generation