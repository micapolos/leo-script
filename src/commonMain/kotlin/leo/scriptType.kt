package leo

val Script.type: Type
  get() =
    type().fold(lineStack.reverse) { plus(it) }

val Script.typeChoice: TypeChoice
  get() =
    choice().fold(lineStack.reverse) { plus(it) }

fun Type.plus(scriptLine: ScriptLine): Type =
  when (scriptLine) {
    is FieldScriptLine -> plus(scriptLine.field)
    is LiteralScriptLine -> plusLine(scriptLine)
  }

fun Type.plus(scriptField: ScriptField): Type =
  when (scriptField.name) {
    choiceName -> type(scriptField.rhs.typeChoice)
    else -> plusLine(line(scriptField))
  }

fun TypeChoice.plus(scriptLine: ScriptLine): TypeChoice =
  plus(scriptLine.typeLine)

fun Type.plusLine(scriptLine: ScriptLine): Type =
  plus(scriptLine.typeLine)

val Script.typeStructure: TypeStructure
  get() =
    lineStack.map { typeLine }.structure

val ScriptLine.typeLine: TypeLine
  get() =
    null
      ?: typeRecursiveOrNull?.line
      ?: typeRecursible.line

val ScriptLine.typeRecursiveOrNull: TypeRecursive?
  get() =
    match(recursiveName) { rhs ->
      rhs.onlyLineOrNull?.typeLine?.recursive
    }

val ScriptLine.typeRecursible: TypeRecursible
  get() =
    null
      ?: typeRecurseOrNull?.recursible
      ?: typeAtom.recursible

val ScriptLine.typeRecurseOrNull: TypeRecurse?
  get() =
    match(recurseName) { rhs ->
      leo.base.notNullIf(rhs.isEmpty) {
        typeRecurse
      }
    }

val ScriptLine.typeAtom: TypeAtom
  get() =
    null
      ?: typeFunctionOrNull?.atom
      ?: typePrimitive.atom

val ScriptLine.typeFunctionOrNull: TypeFunction?
  get() =
    match(functionName) { doing ->
      doing.matchInfix(givesName) { lhs, to ->
        lhs.type functionTo to.type
      }
    }

val ScriptLine.typePrimitive: TypePrimitive
  get() =
    null
      ?: typeNativeOrNull?.primitive
      ?: typeField.primitive

val ScriptLine.typeNativeOrNull: TypeNative?
  get() =
    match(nativeName) { script ->
      native(script)
    }

val ScriptLine.typeField: TypeField
  get() =
    fieldOrNull
      .throwScriptIfNull { script("type" lineTo script("field" lineTo script(this))) }
      .run { name fieldTo rhs.type }