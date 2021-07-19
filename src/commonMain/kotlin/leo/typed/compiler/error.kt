package leo.typed.compiler

import leo.CompileError
import leo.Script

fun compileError(script: Script): Nothing =
  throw CompileError { script }