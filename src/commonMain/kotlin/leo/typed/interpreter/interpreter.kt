package leo.typed.interpreter

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.typed.compiler.Compiler
import leo.typed.compiler.native.Native
import leo.typed.indexed.Evaluated

data class Interpreter(
  val compiler: Compiler<Native>,
  val evaluated: Evaluated<Native>,
  val script: Script)

fun Interpreter.plus(scriptLine: ScriptLine): Interpreter =
  when (scriptLine) {
    is FieldScriptLine -> plus(scriptLine.field)
    is LiteralScriptLine -> plus(scriptLine.literal)
  }

fun Interpreter.plus(scriptField: ScriptField): Interpreter =
  TODO()

fun Interpreter.plus(literal: Literal): Interpreter =
  TODO()