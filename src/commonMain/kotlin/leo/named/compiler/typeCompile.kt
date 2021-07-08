package leo.named.compiler

import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.TypeFunction
import leo.TypeLine
import leo.TypeStructure
import leo.any
import leo.atom
import leo.base.orNullIf
import leo.choiceName
import leo.choiceOrNull
import leo.equalName
import leo.fieldOrNull
import leo.first
import leo.functionOrNull
import leo.getLineOrNull
import leo.getName
import leo.getOrNull
import leo.isName
import leo.lineOrNull
import leo.lineTo
import leo.linkOrNull
import leo.notName
import leo.ofName
import leo.onlyLineOrNull
import leo.plus
import leo.script
import leo.selectName
import leo.structureOrNull
import leo.throwScriptIfNull
import leo.toName

val Type.compileStructure: TypeStructure
  get() =
    structureOrNull.throwScriptIfNull { script("structure" lineTo script) }

val Type.compileChoice: TypeChoice
  get() =
    choiceOrNull.throwScriptIfNull { script("choice" lineTo script) }

val Type.compileLine: TypeLine
  get() =
    compileStructure.compileLine

val TypeStructure.compileLine: TypeLine
  get() =
    onlyLineOrNull.throwScriptIfNull { script("line" lineTo script) }

fun TypeLine.lineOrNull(name: String): TypeLine? =
  structureOrNull?.lineOrNull(name)

val TypeLine.resolveGetOrNull: TypeLine?
  get() =
    atom.fieldOrNull?.let { field ->
      field.rhsType.structureOrNull?.getLineOrNull(field.name)
    }

val Type.compileDoing: TypeFunction
  get() =
    functionOrNull.throwScriptIfNull { script("doing" lineTo script) }

fun <R> Type.check(type: Type, fn: () -> R): R =
  if (this != type) compileError {
    script(
      "type" lineTo script(
        "mismatch" lineTo
            script.plus(
              isName lineTo script(
                notName lineTo script(
                  equalName lineTo script(
                    toName lineTo type.script
                  )
                )
              )
            )
      )
    )
  }
  else fn()

fun Type.get(name: String): Type =
  getOrNull(name).throwScriptIfNull { script.plus(getName lineTo script(name)) }

fun Type.checkOf(type: Type): Type =
  compileLine.let { line ->
    type.compileChoice.lineStack.first { it == line }
      .throwScriptIfNull { script.plus(ofName lineTo type.script) }
      .let { type }
  }

fun checkType(typeStack: Stack<Type>): Type =
  typeStack.linkOrNull
    ?.let { it.head.orNullIf(it.tail.any { this != it.head }) }
    .throwScriptIfNull { script("type stack check") }

val Type.selectChoice: TypeChoice
  get() =
    onlyLineOrNull?.atom?.fieldOrNull?.rhsType?.choiceOrNull
      .throwScriptIfNull { script.plus(selectName lineTo script(choiceName)) }