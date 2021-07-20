package leo

val Type.staticScriptOrNull: Script? get() =
  structureOrNull?.lineStack?.mapOrNull { staticScriptLineOrNull }?.script

val TypeLine.staticScriptLineOrNull: ScriptLine? get() =
  atom.fieldOrNull?.staticScriptLineOrNull

val TypeField.staticScriptLineOrNull: ScriptLine? get() =
  rhsType.staticScriptOrNull?.let { name lineTo it }
