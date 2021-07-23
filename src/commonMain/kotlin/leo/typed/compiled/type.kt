package leo.typed.compiled

import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.TypeStructure
import leo.atom
import leo.base.filterMap
import leo.base.mapIndexed
import leo.base.onlyOrNull
import leo.base.orNullIf
import leo.base.the
import leo.fieldOrNull
import leo.fold
import leo.getFromBottom
import leo.isStatic
import leo.lineSeq
import leo.lineTo
import leo.name
import leo.onlyLineOrNull
import leo.reverse
import leo.script
import leo.seq
import leo.structureOrNull
import leo.typed.compiler.compileError

fun Type.lineIndex(name: String): Int =
  lineIndexOrNull(name) ?: compileError(script("line" lineTo script(name)))

fun Type.lineIndexOrNull(name: String): Int? =
  structureOrNull?.lineSeq?.mapIndexed?.filterMap { orNullIf(value.name != name)?.the }?.onlyOrNull?.index

fun Type.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
  structureOrNull?.lineSeq?.mapIndexed?.filterMap { orNullIf(value.name != name)?.the }?.onlyOrNull

val Type.rhsOrNull: Type? get() =
  structureOrNull?.onlyLineOrNull?.atom?.fieldOrNull?.rhsType

fun Type.getLineOrNull(index: Int): TypeLine? =
  rhsOrNull?.structureOrNull?.lineStack?.getFromBottom(index)

fun TypeChoice.indexedLineOrNull(name: String): IndexedValue<TypeLine>? =
  lineStack.reverse.seq.mapIndexed.filterMap { orNullIf(value.name != name)?.the }.onlyOrNull

val Type.compileStructure: TypeStructure
  get() =
    structureOrNull ?: compileError(script("structure"))

val Type.compileLine: TypeLine get() =
  onlyLineOrNull ?: compileError(script("line"))

val TypeStructure.dynamicLineCount: Int get() =
  0.fold(lineStack) { plus(it.dynamicLineCount) }

val TypeLine.dynamicLineCount: Int get() =
  if (isStatic) 0 else 1