package leo.named.compiler

import leo.ChoiceType
import leo.StructureType
import leo.Type
import leo.TypeChoice
import leo.TypeField
import leo.TypeLine
import leo.TypeStructure
import leo.any
import leo.atomOrNull
import leo.fieldOrNull
import leo.onlyLineOrNull
import leo.recursibleOrNull
import leo.structureOrNull

fun Type.choiceContains(type: Type): Boolean =
  when (this) {
    is ChoiceType -> choice.contains(type)
    is StructureType -> structure.choiceContains(type)
  }

fun TypeStructure.choiceContains(type: Type): Boolean =
  onlyLineOrNull?.recursibleOrNull?.atomOrNull?.fieldOrNull?.run {
    type.structureOrNull?.onlyLineOrNull?.recursibleOrNull?.atomOrNull?.fieldOrNull?.let {
      choiceContains(it)
    }
  } ?: false

fun TypeField.choiceContains(field: TypeField): Boolean =
  name == field.name && rhsType.choiceContains(field.rhsType)

fun TypeChoice.contains(type: Type): Boolean =
  type.onlyLineOrNull?.let { contains(it) } ?: false

fun TypeChoice.contains(line: TypeLine): Boolean =
  lineStack.any { equals(line) }


