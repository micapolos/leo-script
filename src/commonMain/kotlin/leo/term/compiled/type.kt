package leo.term.compiled

import leo.Type
import leo.TypeChoice
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
import leo.lineTo
import leo.nameOrNull
import leo.onlyLineOrNull
import leo.reverse
import leo.script
import leo.seq
import leo.structureOrNull
import leo.term.compiler.compileError

fun Type.lineIndex(name: String): Int =
  lineIndexOrNull(name) ?: compileError(script("line" lineTo script(name)))

fun Type.lineIndexOrNull(name: String): Int? =
  structureOrNull?.lineSeq?.mapIndexed?.filterMap { orNullIf(value.nameOrNull != name)?.the }?.onlyOrNull?.index

fun Type.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
  structureOrNull?.lineSeq?.mapIndexed?.filterMap { orNullIf(value.nameOrNull != name)?.the }?.onlyOrNull

val Type.contentOrNull: Type? get() =
  structureOrNull?.onlyLineOrNull?.atom?.fieldOrNull?.rhsType

fun Type.getLineOrNull(index: Int): TypeLine? =
  contentOrNull?.structureOrNull?.lineStack?.getFromBottom(index)

fun TypeChoice.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
  lineStack.reverse.seq.mapIndexed.filterMap { orNullIf(value.nameOrNull != name)?.the }?.onlyOrNull