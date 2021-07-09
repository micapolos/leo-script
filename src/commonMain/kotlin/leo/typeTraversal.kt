package leo

import leo.base.notNullIf

val TypeLine.recursible: TypeRecursible
  get() =
    when (this) {
      is RecursibleTypeLine -> recursible
      is RecursiveTypeLine -> recursive.line.shiftRecursion.recursible
    }

val TypeLine.atom: TypeAtom
  get() =
    recursible.atomOrNull!! // TODO: Incorporate into recursible, so there's no unsafety

// ========================================

val TypeLine.shiftRecursion: TypeLine
  get() =
    when (this) {
      is RecursibleTypeLine -> recursible.shiftRecursion.line
      is RecursiveTypeLine -> this
    }

val TypeRecursible.shiftRecursion: TypeRecursible
  get() =
    when (this) {
      is AtomTypeRecursible -> atom.shiftRecursion.recursible
      is RecurseTypeRecursible -> error("$this.shiftRecursion")
    }

val TypeAtom.shiftRecursion: TypeAtom
  get() =
    when (this) {
      is FunctionTypeAtom -> this
      is PrimitiveTypeAtom -> primitive.shiftRecursion.atom
    }

val TypePrimitive.shiftRecursion: TypePrimitive
  get() =
    when (this) {
      is FieldTypePrimitive -> field.shiftRecursion.primitive
      is AnyTypePrimitive -> this
    }

val TypeField.shiftRecursion: TypeField
  get() =
    name fieldTo rhsType.shiftRecursionWithName(name)

fun Type.shiftRecursionWithName(name: String): Type =
  when (this) {
    is ChoiceType -> choice.shiftRecursionWithName(name).type
    is StructureType -> structure.shiftRecursionWithName(name).type
  }

fun TypeStructure.shiftRecursionWithName(name: String): TypeStructure =
  lineStack.shiftRecursionWithName(name).structure

fun TypeChoice.shiftRecursionWithName(name: String): TypeChoice =
  lineStack.shiftRecursionWithName(name).choice

@kotlin.jvm.JvmName("typeLineShiftRecursionWithName")
fun Stack<TypeLine>.shiftRecursionWithName(name: String): Stack<TypeLine> =
  mapRope { rope ->
    rope.current
      .replaceNonRecursiveOrNull(
        line(recursible(typeRecurse)),
        name lineTo rope
          .updateCurrent { line(recursible(typeRecurse)) }
          .stack.structure.type)
      ?.let { line(recursive(it)) }
      ?: rope.current
  }

// =====================================================

fun Type.make(name: String): Type =
  (name lineTo this).unshiftRecursion.structure.type

val TypeLine.unshiftRecursion: TypeLine
  get() =
    unshiftRecursionOrNull?.let { line(recursive(it)) } ?: this

val TypeLine.unshiftRecursionOrNull: TypeLine?
  get() =
    when (this) {
      is RecursibleTypeLine -> recursible.unshiftRecursionOrNull?.line
      is RecursiveTypeLine -> null
    }

val TypeRecursible.unshiftRecursionOrNull: TypeRecursible?
  get() =
    when (this) {
      is AtomTypeRecursible -> atom.unshiftRecursion?.recursible
      is RecurseTypeRecursible -> null
    }

val TypeAtom.unshiftRecursion: TypeAtom?
  get() =
    when (this) {
      is FunctionTypeAtom -> null
      is PrimitiveTypeAtom -> primitive.unshiftRecursion?.atom
    }

val TypePrimitive.unshiftRecursion: TypePrimitive?
  get() =
    when (this) {
      is FieldTypePrimitive -> field.unshiftRecursion?.primitive
      is AnyTypePrimitive -> null
    }

val TypeField.unshiftRecursion: TypeField?
  get() =
    rhsType.unshiftRecursionOrNullWithName(name)?.let { name fieldTo it }

fun Type.unshiftRecursionOrNullWithName(name: String): Type? =
  when (this) {
    is ChoiceType -> choice.unshiftRecursionOrNullWithName(name)?.type
    is StructureType -> structure.unshiftRecursionOrNullWithName(name)?.type
  }

fun TypeStructure.unshiftRecursionOrNullWithName(name: String): TypeStructure? =
  lineStack.unshiftRecursionOrNullWithName(name)?.structure

fun TypeChoice.unshiftRecursionOrNullWithName(name: String): TypeChoice? =
  lineStack.unshiftRecursionOrNullWithName(name)?.choice

@kotlin.jvm.JvmName("typeLineUnshiftRecursionWithName")
fun Stack<TypeLine>.unshiftRecursionOrNullWithName(name: String): Stack<TypeLine>? =
  notNullIf(canUnshiftRecursionWithName(name)) {
    mapRope { rope ->
      rope.current.recursiveOrNull?.line
        ?.replaceNonRecursiveOrNull(
          name lineTo rope
            .updateCurrent { line(recursible(typeRecurse)) }
            .stack.structure.type,
          line(recursible(typeRecurse)))
        ?.let { it }
        ?: rope.current
    }
  }

fun Stack<TypeLine>.canUnshiftRecursionWithName(name: String): Boolean =
  mapRope { rope ->
    rope.current.recursiveOrNull
      ?.line
      ?.replaceNonRecursiveOrNull(
        name lineTo rope
          .updateCurrent { line(recursible(typeRecurse)) }
          .stack.structure.type,
        line(recursible(typeRecurse)))
  }.any { this != null }

// =====================================================

fun Type.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): Type? =
  when (this) {
    is ChoiceType -> choice.replaceNonRecursiveOrNull(line, newLine)?.type
    is StructureType -> structure.replaceNonRecursiveOrNull(line, newLine)?.type
  }

fun TypeChoice.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeChoice? =
  lineStack.replaceNonRecursiveOrNull(line, newLine)?.choice

fun TypeStructure.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeStructure? =
  lineStack.replaceNonRecursiveOrNull(line, newLine)?.structure

fun Stack<TypeLine>.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): Stack<TypeLine>? =
  notNullIf(canReplaceNonRecursive(line, newLine)) {
    map { replaceNonRecursiveOrNull(line, newLine) ?: this }
  }

fun Stack<TypeLine>.canReplaceNonRecursive(line: TypeLine, newLine: TypeLine): Boolean =
  any { replaceNonRecursiveOrNull(line, newLine) != null }

fun TypeLine.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeLine? =
  if (this == line) newLine
  else when (this) {
    is RecursibleTypeLine -> recursible.replaceNonRecursiveOrNull(line, newLine)?.line
    is RecursiveTypeLine -> null
  }

fun TypeRecursible.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeRecursible? =
  when (this) {
    is AtomTypeRecursible -> atom.replaceNonRecursiveOrNull(line, newLine)?.recursible
    is RecurseTypeRecursible -> null
  }

fun TypeAtom.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeAtom? =
  when (this) {
    is FunctionTypeAtom -> null
    is PrimitiveTypeAtom -> primitive.replaceNonRecursiveOrNull(line, newLine)?.atom
  }

fun TypePrimitive.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypePrimitive? =
  when (this) {
    is FieldTypePrimitive -> field.replaceNonRecursiveOrNull(line, newLine)?.primitive
    is AnyTypePrimitive -> null
  }

fun TypeField.replaceNonRecursiveOrNull(line: TypeLine, newLine: TypeLine): TypeField? =
  rhsType.replaceNonRecursiveOrNull(line, newLine)?.let { name fieldTo it }
