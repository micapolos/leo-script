package leo.expression.compiler

import leo.Type
import leo.TypeStructure
import leo.atomOrNull
import leo.fieldOrNull
import leo.getOrNull
import leo.onlyLineOrNull
import leo.structureOrNull
import leo.type

val TypeStructure.resolveGetOrNull: TypeStructure? get() =
	onlyLineOrNull?.atomOrNull?.fieldOrNull?.let { field ->
		field.rhsType.structureOrNull?.getOrNull(field.name)
	}

val TypeStructure.resolveOrNull: TypeStructure? get() =
	null
		?: resolveGetOrNull

val TypeStructure.resolve: TypeStructure get() =
	resolveOrNull ?: this

val Type.resolve: Type get() =
	structureOrNull?.resolve?.type ?: this
