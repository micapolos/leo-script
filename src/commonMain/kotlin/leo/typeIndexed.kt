package leo

val Type.lineCount: Int get() =
  when (this) {
    is ChoiceType -> 1
    is StructureType -> structure.lineStack.size
  }
