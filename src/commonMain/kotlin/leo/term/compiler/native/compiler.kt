package leo.term.compiler.native

import leo.term.compiler.Compiler
import leo.term.compiler.compiler
import leo.term.compiler.context
import leo.term.compiler.local
import leo.term.compiler.module

val nativeCompiler: Compiler<Native> get() =
  nativeEnvironment.context.module.local.compiler