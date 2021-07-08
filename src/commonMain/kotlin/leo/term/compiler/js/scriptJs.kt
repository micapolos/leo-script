package leo.term.compiler.js

import leo.Script
import leo.term.compiler.typedTerm

val Script.js: Js
  get() =
    jsEnvironment.typedTerm(this).v.js