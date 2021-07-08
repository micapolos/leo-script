package leo

val TypeStructure.resolveGetOrNull: TypeStructure?
  get() =
    onlyLineOrNull?.atomOrNull?.fieldOrNull?.let { field ->
      field.rhsType.structureOrNull?.getOrNull(field.name)
    }

val Type.resolveGetOrNull: Type?
  get() =
    structureOrNull?.resolveGetOrNull?.type