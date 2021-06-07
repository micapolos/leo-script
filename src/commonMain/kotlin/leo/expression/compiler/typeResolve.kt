package leo.expression.compiler

import leo.Type
import leo.TypeStructure
import leo.atomOrNull
import leo.fieldOrNull
import leo.getOrNull
import leo.lineTo
import leo.numberName
import leo.numberTypeLine
import leo.onlyLineOrNull
import leo.structure
import leo.structureOrNull
import leo.textName
import leo.textTypeLine
import leo.type

val TypeStructure.resolveGetOrNull: TypeStructure? get() =
	onlyLineOrNull?.atomOrNull?.fieldOrNull?.let { field ->
		field.type.structureOrNull?.getOrNull(field.name)
	}

val TypeStructure.resolveNativeOrNull: TypeStructure? get() =
	when (this) {
		structure(textName lineTo type()) -> structure(textTypeLine)
		structure(numberName lineTo type()) -> structure(numberTypeLine)
		else -> null
	}

val TypeStructure.resolveOrNull: TypeStructure? get() =
	null
		?: resolveGetOrNull
		?: resolveNativeOrNull

val TypeStructure.resolve: TypeStructure get() =
	resolveOrNull ?: this

val Type.resolve: Type get() =
	structureOrNull?.resolve?.type ?: this
