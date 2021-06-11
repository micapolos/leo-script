package leo.indexed

import leo.Type
import leo.TypeLine
import leo.TypeStructure
import leo.base.firstOrNull
import leo.base.mapIndexed
import leo.base.reverse
import leo.name
import leo.onlyLineOrNull
import leo.seq
import leo.structureOrNull

fun TypeStructure.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
	lineStack.seq.reverse.mapIndexed.reverse.firstOrNull { value.name == name }

fun TypeLine.getIndexedLineOrNull(name: String): IndexedValue<TypeLine>? =
	structureOrNull?.indexedLineOrNull(name)

fun Type.getIndexedLineOrNull(name: String): IndexedValue<TypeLine>? =
	onlyLineOrNull?.getIndexedLineOrNull(name)
