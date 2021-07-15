package leo

import leo.base.mapIndexed

fun TypeStructure.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
  lineStack.reverse.seq.mapIndexed.firstOrNull { it.value.nameOrNull == name }
