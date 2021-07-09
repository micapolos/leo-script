package leo.term.compiler.idris

import leo.Script
import leo.term.compiler.typedTerm

val Script.idris: Idris
  get() =
    idrisEnvironment.typedTerm(this).v.idris