package leo.indexed.compiler

import leo.Type
import leo.TypeStructure
import leo.base.notNullOrError
import leo.structureOrNull

val Type.compileStructure: TypeStructure get() =
	structureOrNull.notNullOrError("$this not structure")
