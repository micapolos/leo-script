package leo.term.compiler.python

import leo.Script
import leo.term.compiler.typedTerm

val Script.python: Python
  get() =
    pythonEnvironment.typedTerm(this).v.python