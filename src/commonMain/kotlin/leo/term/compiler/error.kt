package leo.term.compiler

import leo.Script
import leo.named.compiler.CompileError

fun compileError(script: Script): Nothing =
  throw CompileError { script }