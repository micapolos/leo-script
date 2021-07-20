package leo.typed.indexed.native

import leo.Script
import leo.typed.compiled.Compiled
import leo.typed.compiler.native.Native

val Compiled<Native>.valueScript: Script
  get() =
    value.script(type)