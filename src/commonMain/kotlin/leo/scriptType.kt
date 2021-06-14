package leo

import leo.type.compiler.numberTypeScriptLine
import leo.type.compiler.textTypeScriptLine
import leo.type.compiler.typeLiteralOrNull

val Script.type: Type get() =
	null
		?: typeChoiceOrNull?.type
		?: typeStructure.type

val Script.typeChoiceOrNull: TypeChoice? get() =
	matchPrefix(choiceName) { rhs ->
		rhs.lineStack.map { typeLine }.choice
	}

val Script.typeStructure: TypeStructure get() =
	lineStack.map { typeLine }.structure

val ScriptLine.typeLine: TypeLine get() =
	null
		?: typeRecursiveOrNull?.line
		?: typeRecursible.line

val ScriptLine.typeRecursiveOrNull: TypeRecursive? get() =
	match(recursiveName) { rhs ->
		rhs.onlyLineOrNull?.typeLine?.recursive
	}

val ScriptLine.typeRecursible: TypeRecursible get() =
	null
		?: typeRecurseOrNull?.recursible
		?: typeAtom.recursible

val ScriptLine.typeRecurseOrNull: TypeRecurse? get() =
	match(recurseName) { rhs ->
		leo.base.notNullIf(rhs.isEmpty) {
			typeRecurse
		}
	}

val ScriptLine.typeAtom: TypeAtom get() =
	null
		?: typeDoingOrNull?.atom
		?: typePrimitive.atom

val ScriptLine.typeDoingOrNull: TypeDoing? get() =
	match(doingName) { rhs ->
		rhs.matchInfix(toName) { lhs, rhs ->
			lhs.type doing rhs.type
		}
	}

val ScriptLine.typePrimitive: TypePrimitive get() =
	null
		?: typeLiteralOrNull?.primitive
		?: typeField.primitive

val ScriptLine.typeLiteralOrNull: TypeLiteral? get() =
	when (this) {
		textTypeScriptLine -> literal(typeText)
		numberTypeScriptLine -> literal(typeNumber)
		else -> null
	}

val ScriptLine.typeField: TypeField get() =
	fieldOrNull
		.throwScriptIfNull { script("type" lineTo script("field" lineTo script(this))) }
		.run { name fieldTo rhs.type }