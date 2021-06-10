package leo.indexed

import leo.Type
import leo.TypeLine
import leo.TypeStructure
import leo.base.firstOrNull
import leo.base.map
import leo.base.mapIndexed
import leo.base.reverse
import leo.name
import leo.onlyLineOrNull
import leo.seq
import leo.structureOrNull
import leo.type

fun TypeStructure.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
	lineStack.seq.reverse.mapIndexed.reverse.firstOrNull { value.name == name }

fun TypeLine.getIndexedOrNull(name: String): IndexedValue<TypeLine>? =
	structureOrNull?.indexedLineOrNull(name)

fun Type.getIndexedOrNull(name: String): IndexedValue<Type>? =
	structureOrNull?.onlyLineOrNull?.getIndexedOrNull(name)?.map { type(it) }
