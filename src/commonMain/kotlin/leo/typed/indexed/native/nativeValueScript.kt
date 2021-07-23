package leo.typed.indexed.native

import leo.Script
import leo.ScriptLine
import leo.Type
import leo.line
import leo.literal
import leo.typed.compiler.native.DoubleNative
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.StringNative
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeScriptLineOrNull
import leo.typed.compiler.native.nativeTextTypeLine
import leo.typed.compiler.native.scriptLine
import leo.typed.indexed.Value
import leo.typed.indexed.ValueScriptContext
import leo.typed.indexed.native
import leo.typed.indexed.script

val nativeValueScriptContext: ValueScriptContext<Native>
  get() = ValueScriptContext(
    { native -> native.nativeScriptLine },
    { value, typeLine ->
      when (typeLine) {
        nativeTextTypeLine -> value.native.nativeScriptLine
        nativeNumberTypeLine -> value.native.nativeScriptLine
        else -> null
      }
    },
    { typeLine -> typeLine.nativeScriptLineOrNull })

fun Value<Native>.script(type: Type): Script =
  script(type, nativeValueScriptContext)

val Native.nativeScriptLine: ScriptLine
  get() =
    when (this) {
      is DoubleNative ->
        line(literal(double))
      is StringNative ->
        line(literal(string))
      else -> scriptLine
    }
