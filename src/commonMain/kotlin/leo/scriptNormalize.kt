package leo

val Script.normalize: Script
  get() =
    when (this) {
      is UnitScript -> this
      is LinkScript -> script(link.normalize)
    }

val ScriptLink.normalize: ScriptLink
  get() =
    line.fieldOrNull?.onlyNameOrNull?.let { script() linkTo line(it fieldTo lhs.normalize) }
      ?: lhs.normalize linkTo line.normalize

val ScriptLine.normalize: ScriptLine
  get() =
    when (this) {
      is FieldScriptLine -> line(field.normalize)
      is LiteralScriptLine -> this
    }

val ScriptField.normalize: ScriptField
  get() =
    name fieldTo rhs.normalize
