package leo.type.compiler

import leo.TypeLine
import leo.TypeStructure
import leo.numberName
import leo.numberTypeLine
import leo.structureOrNull
import leo.textName
import leo.textTypeLine
import leo.typeStructure

val String.nameTypeLineOrNull: TypeLine? get() =
	when (this) {
		textName -> textTypeLine
		numberName -> numberTypeLine
		else -> null
	}

val String.nameResolveTypeStructureOrNull: TypeStructure? get() =
	nameTypeLineOrNull?.structureOrNull

val String.nameResolveTypeStructure: TypeStructure get() =
	null
		?: nameResolveTypeStructureOrNull
		?: typeStructure
