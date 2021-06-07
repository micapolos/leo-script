package leo.type.compiler

import leo.Type
import leo.TypeLine
import leo.TypeStructure
import leo.base.notNullOrError
import leo.plus
import leo.structureOrNull
import leo.type

fun Type.compilePlus(line: TypeLine): Type =
	compileStructure.plus(line).type

val Type.compileStructure: TypeStructure get() =
	structureOrNull.notNullOrError("$this is not a structure")