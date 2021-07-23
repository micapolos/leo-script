package leo

import leo.base.mapIndexed

fun TypeStructure.indexOrNull(name: String): Int? =
  indexedLineOrNull(name)?.index

fun TypeStructure.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
  lineStack.reverse.seq.mapIndexed.firstOrNull { it.value.name == name }
