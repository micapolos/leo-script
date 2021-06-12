package leo.named.compiler

import leo.TypeLine
import leo.TypeStructure
import leo.atom
import leo.base.notNullOrError
import leo.fieldOrNull
import leo.getLineOrNull
import leo.lineOrNull
import leo.onlyLineOrNull
import leo.structureOrNull

val TypeStructure.typeLine: TypeLine get() =
	onlyLineOrNull.notNullOrError("$this.typeLine")

fun TypeLine.lineOrNull(name: String): TypeLine? =
	structureOrNull?.lineOrNull(name)

val TypeLine.resolveGetOrNull: TypeLine? get() =
	atom.fieldOrNull?.let { field ->
		field.rhsType.structureOrNull?.getLineOrNull(field.name)
	}
