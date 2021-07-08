package leo

val Script.string
  get() =
    notation.string

val String.preprocess: String
  get() =
    convertTabsToSpaces

val String.convertTabsToSpaces get() = replace("\t", "  ")

val String.addMissingNewline
  get() =
    if (isEmpty() || this[length - 1] == '\n') this
    else plus('\n')
