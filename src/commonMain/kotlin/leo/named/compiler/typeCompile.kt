package leo.named.compiler

import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.TypeStructure
import leo.atom
import leo.base.notNullOrError
import leo.choiceOrNull
import leo.fieldOrNull
import leo.getLineOrNull
import leo.lineOrNull
import leo.onlyLineOrNull
import leo.structureOrNull

val Type.compileStructure: TypeStructure get() =
	structureOrNull.notNullOrError("$this not a structure")

val Type.compileChoice: TypeChoice
	get() =
		choiceOrNull.notNullOrError("$this not a choice")

val Type.compileLine: TypeLine get() =
	compileStructure.compileLine

val TypeStructure.compileLine: TypeLine get() =
	onlyLineOrNull.notNullOrError("$this.typeLine")

fun TypeLine.lineOrNull(name: String): TypeLine? =
	structureOrNull?.lineOrNull(name)

val TypeLine.resolveGetOrNull: TypeLine? get() =
	atom.fieldOrNull?.let { field ->
		field.rhsType.structureOrNull?.getLineOrNull(field.name)
	}
