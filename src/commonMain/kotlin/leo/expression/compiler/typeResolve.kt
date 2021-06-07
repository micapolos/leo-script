package leo.expression.compiler

import leo.TypeStructure
import leo.atomOrNull
import leo.fieldOrNull
import leo.getOrNull
import leo.onlyLineOrNull
import leo.structureOrNull

val TypeStructure.resolveGetOrNull: TypeStructure? get() =
	onlyLineOrNull?.atomOrNull?.fieldOrNull?.let { field ->
		field.type.structureOrNull?.getOrNull(field.name)
	}