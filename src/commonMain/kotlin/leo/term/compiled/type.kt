package leo.term.compiled

import leo.Type
import leo.TypeLine
import leo.atom
import leo.base.filterMap
import leo.base.mapIndexed
import leo.base.onlyOrNull
import leo.base.orNullIf
import leo.base.the
import leo.fieldOrNull
import leo.getFromBottom
import leo.lineSeq
import leo.name
import leo.onlyLineOrNull
import leo.structureOrNull

fun Type.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
  structureOrNull?.lineSeq?.mapIndexed?.filterMap { orNullIf(value.name != name)?.the }?.onlyOrNull

val Type.contentOrNull: Type? get() =
  structureOrNull?.onlyLineOrNull?.atom?.fieldOrNull?.rhsType

fun Type.getLineOrNull(index: Int): TypeLine? =
  contentOrNull?.structureOrNull?.lineStack?.getFromBottom(index)
