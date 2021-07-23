package leo

import leo.base.runIf

typealias Fn = (TypeLine) -> ScriptLine?
val nullFn: Fn get() = { null }

val Type.scriptLine: ScriptLine get() = "type" lineTo script

val Type.script: Script get() = script()
val TypeLine.scriptLine: ScriptLine get() = scriptLine()

fun Type.script(fn: Fn = { null }): Script =
  when (this) {
    is StructureType -> structure.script(fn)
    is ChoiceType -> choice.script(fn)
  }

fun TypeStructure.script(fn: Fn = { null }): Script =
  lineStack.map { scriptLine(fn) }.script

fun TypeChoice.script(fn: Fn = { null }): Script =
  script(choiceName lineTo lineStack.map { scriptLine(fn) }.script)

fun TypeLine.scriptLine(fn: Fn = { null }): ScriptLine =
  fn(this) ?: when (this) {
    is RecursiveTypeLine -> recursive.scriptLine(fn)
    is RecursibleTypeLine -> recursible.scriptLine(fn)
  }

fun TypeRecursible.scriptLine(fn: Fn = { null }): ScriptLine =
    when (this) {
      is AtomTypeRecursible -> atom.scriptLine(fn)
      is RecurseTypeRecursible -> recurse.scriptLine(fn)
    }

fun TypeAtom.scriptLine(fn: Fn = { null }): ScriptLine =
    when (this) {
      is FunctionTypeAtom -> function.scriptLine(fn)
      is PrimitiveTypeAtom -> primitive.scriptLine(fn)
    }

fun TypePrimitive.scriptLine(fn: Fn = { null }): ScriptLine =
    when (this) {
      is FieldTypePrimitive -> field.scriptLine(fn)
      is NativeTypePrimitive -> native_.scriptLine(fn)
    }

fun TypeField.scriptLine(fn: Fn = { null }): ScriptLine =
    unescapedScriptLine(fn).runIf(name.isTypeKeyword) { theName lineTo script(this) }

fun TypeField.unescapedScriptLine(fn: Fn = { null }): ScriptLine =
    name lineTo rhsType.script(fn)

fun TypeFunction.scriptLine(fn: Fn = { null }): ScriptLine =
    functionName lineTo lhsType.script(fn).plus(doingName lineTo rhsType.script(fn))

fun TypeRecursive.scriptLine(fn: Fn = { null }): ScriptLine =
    recursiveName lineTo script(line.scriptLine(fn))

@Suppress("unused")
fun TypeRecurse.scriptLine(@Suppress("UNUSED_PARAMETER") fn: Fn = { null }): ScriptLine =
    recurseName lineTo script()

val String.isTypeKeyword: Boolean
  get() =
    when (this) {
      eitherName -> true
      functionName -> true
      recurseName -> true
      recursiveName -> true
      theName -> true
      else -> false
    }

fun TypeNative.scriptLine(@Suppress("UNUSED_PARAMETER") fn: Fn = { null }): ScriptLine =
  nativeName lineTo script
