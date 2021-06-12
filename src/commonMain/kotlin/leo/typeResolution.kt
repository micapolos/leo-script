package leo

val TypeStructure.resolveGetOrNull: TypeStructure? get() =
	onlyLineOrNull?.atomOrNull?.fieldOrNull?.let { field ->
		field.rhsType.structureOrNull?.getOrNull(field.name)
	}
