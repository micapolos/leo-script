package leo.typed.interpreter

import leo.typed.compiler.Compiler
import leo.typed.compiler.native.Native
import leo.typed.indexed.Evaluated

data class Interpreter<V>(
  val compiler: Compiler<V>,
  val evaluated: Evaluated<Native>)