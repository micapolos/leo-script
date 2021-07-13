package leo.term.compiler.native

import leo.term.compiled.nativeLine
import leo.term.compiler.Environment

val nativeEnvironment: Environment<Native>
  get() =
    Environment(
      { literal -> nativeLine(literal.native) },
      { typedTerm -> null },
      { it.scriptLine })
