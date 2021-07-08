package leo.term.compiler.julia

import leo.Script
import leo.term.compiler.typedTerm

val Script.julia: Julia
  get() =
    juliaEnvironment.typedTerm(this).v.julia