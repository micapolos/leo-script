package leo.term.compiler.scheme

import leo.Script
import leo.term.compiler.typedTerm
import scheme.Scheme

val Script.scheme: Scheme
  get() =
    schemeEnvironment.typedTerm(this).v.scheme