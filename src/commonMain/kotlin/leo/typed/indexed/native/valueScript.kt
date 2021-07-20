package leo.typed.indexed.native

import leo.Script
import leo.Type
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextTypeLine
import leo.typed.indexed.Value
import leo.typed.indexed.ValueScriptContext
import leo.typed.indexed.native
import leo.typed.indexed.script
import leo.typed.indexed.scriptLine

val nativeContext: ValueScriptContext<Native>
  get() = ValueScriptContext(
  { native -> native.scriptLine },
  { value, typeLine ->
    when (typeLine) {
      nativeTextTypeLine -> value.native.scriptLine
      nativeNumberTypeLine -> value.native.scriptLine
      else -> null
    }
  })

fun Value<Native>.script(type: Type): Script =
  script(type, nativeContext)
