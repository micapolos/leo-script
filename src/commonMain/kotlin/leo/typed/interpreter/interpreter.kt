package leo.typed.interpreter

import leo.typed.compiler.Compiler
import leo.typed.compiler.native.Native
import leo.typed.indexed.Evaluator

data class Interpreter<V>(
  val compiler: Compiler<V>,
  val evaluator: Evaluator<Native>)