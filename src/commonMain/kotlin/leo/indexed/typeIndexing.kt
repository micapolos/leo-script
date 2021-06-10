package leo.indexed

import leo.TypeLine
import leo.TypeStructure
import leo.base.firstOrNull
import leo.base.mapIndexed
import leo.base.reverse
import leo.name
import leo.seq

fun TypeStructure.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
	lineStack.seq.reverse.mapIndexed.reverse.firstOrNull { value.name == name }

