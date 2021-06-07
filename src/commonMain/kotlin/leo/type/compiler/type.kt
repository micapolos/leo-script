package leo.type.compiler

import leo.ChoiceType
import leo.StructureType
import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.TypeStructure
import leo.base.notNullOrError
import leo.choice
import leo.isEmpty
import leo.onlyLineOrNull
import leo.plus
import leo.structureOrNull
import leo.type

fun Type.compilePlus(line: TypeLine): Type =
	compileStructure.plus(line).type

val Type.compileStructure: TypeStructure get() =
	structureOrNull.notNullOrError("$this is not a structure")

val Type.compileLine: TypeLine get() =
	compileStructure.compileLine

val TypeStructure.compileLine: TypeLine get() =
	onlyLineOrNull.notNullOrError("$this not a type line")

val TypeStructure.compileChoice: TypeChoice get() =
	if (isEmpty) choice()
	else choice(compileLine)

val Type.compileChoice: TypeChoice get() =
	when (this) {
		is ChoiceType -> choice
		is StructureType -> structure.compileChoice
	}
