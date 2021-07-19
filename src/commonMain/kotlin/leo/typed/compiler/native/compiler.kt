package leo.typed.compiler.native

import leo.typed.compiler.Compiler
import leo.typed.compiler.block
import leo.typed.compiler.compiler
import leo.typed.compiler.context
import leo.typed.compiler.module

val nativeCompiler: Compiler<Native> get() =
  nativeEnvironment.context.module.block.compiler
