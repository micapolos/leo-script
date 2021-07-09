package leo.term.compiler.haskell

import leo.Script
import leo.term.compiler.typedTerm

val Script.haskell: Haskell
  get() =
    haskellEnvironment.typedTerm(this).v.haskell