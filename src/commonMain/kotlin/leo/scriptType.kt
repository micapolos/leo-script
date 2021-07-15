package leo

import leo.base.orIfNull
import leo.term.compiler.compileError
import leo.term.typed.pickDropChoiceOrNull

val anyTextScriptLine get() = anyName lineTo script(textName)
val anyNumberScriptLine get() = anyName lineTo script(numberName)

val Script.type: Type
  get() =
    type().fold(lineStack.reverse) { plus(it) }

fun Type.plus(scriptLine: ScriptLine): Type =
  when (scriptLine) {
    is FieldScriptLine -> plus(scriptLine.field)
    is LiteralScriptLine -> plusLine(scriptLine)
  }

fun Type.plus(scriptField: ScriptField): Type =
  if (scriptField.name == eitherName) plusEither(scriptField.rhs)
  else plusLine(line(scriptField))

fun Type.plusEither(script: Script): Type =
  pickDropChoiceOrNull
    .orIfNull { compileError(script("either")) }
    .plus(script.type.onlyLineOrNull.orIfNull { compileError(script("either")) })
    .type

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
      ?: typeAnyOrNull?.primitive
      ?: typeField.primitive

val ScriptLine.typeAnyOrNull: TypeAny?
  get() =
    match(anyName) { script ->
      any(script)
    }

val ScriptLine.typeField: TypeField
  get() =
    fieldOrNull
      .throwScriptIfNull { script("type" lineTo script("field" lineTo script(this))) }
      .run { name fieldTo rhs.type }