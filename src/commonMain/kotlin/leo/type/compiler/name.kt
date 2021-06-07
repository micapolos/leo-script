package leo.type.compiler

import leo.Type
import leo.TypeLine
import leo.numberName
import leo.numberTypeLine
import leo.structureOrNull
import leo.textName
import leo.textTypeLine
import leo.type

val String.literalTypeLineOrNull: TypeLine? get() =
	when (this) {
		textName -> textTypeLine
		numberName -> numberTypeLine
		else -> null
	}

val String.nameResolveTypeOrNull: Type? get() =
	literalTypeLineOrNull?.structureOrNull?.type

val String.nameResolveType: Type get() =
	null
		?: nameResolveTypeOrNull
		?: type
