package leo.kotlin

import leo.AtomTypeLine
import leo.DoingTypeAtom
import leo.FieldTypeAtom
import leo.LiteralTypeAtom
import leo.NumberTypeLiteral
import leo.RecurseTypeLine
import leo.RecursiveTypeLine
import leo.TextTypeLiteral
import leo.TypeAtom
import leo.TypeLine
import leo.TypeLiteral
import leo.base.titleCase
import leo.doingName
import leo.get
import leo.numberName
import leo.textName

val TypeLine.className: String get() =
	classNameGeneration.get(types())

val TypeLine.classNameGeneration: Generation<String> get() =
	when (this) {
		is AtomTypeLine -> atom.classNameGeneration
		is RecurseTypeLine -> TODO()
		is RecursiveTypeLine -> TODO()
	}

val TypeAtom.classNameGeneration: Generation<String> get() =
	when (this) {
		is DoingTypeAtom -> doingName.titleCase.generation
		is FieldTypeAtom -> field.typeNameGeneration
		is LiteralTypeAtom -> literal.classNameGeneration
	}

val TypeLiteral.classNameGeneration: Generation<String> get() =
	when (this) {
		is NumberTypeLiteral -> numberName.titleCase.generation
		is TextTypeLiteral -> textName.titleCase.generation
	}