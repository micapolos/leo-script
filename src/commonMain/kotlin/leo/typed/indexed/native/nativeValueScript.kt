package leo.typed.indexed.native

import leo.Script
import leo.ScriptLine
import leo.Type
import leo.line
import leo.lineTo
import leo.literal
import leo.nativeName
import leo.script
import leo.typed.compiler.native.DoubleIsLessThanDoubleNative
import leo.typed.compiler.native.DoubleMinusDoubleNative
import leo.typed.compiler.native.DoubleNative
import leo.typed.compiler.native.DoublePlusDoubleNative
import leo.typed.compiler.native.DoubleStringNative
import leo.typed.compiler.native.DoubleTimesDoubleNative
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.ObjectEqualsObjectNative
import leo.typed.compiler.native.StringLengthNative
import leo.typed.compiler.native.StringNative
import leo.typed.compiler.native.StringPlusStringNative
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextTypeLine
import leo.typed.indexed.Value
import leo.typed.indexed.ValueScriptContext
import leo.typed.indexed.native
import leo.typed.indexed.script

val nativeValueScriptContext: ValueScriptContext<Native>
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
  script(type, nativeValueScriptContext)

val Native.scriptLine: ScriptLine
  get() =
  when (this) {
    is DoubleNative ->
      line(literal(double))
    is StringNative ->
      line(literal(string))
    DoubleIsLessThanDoubleNative ->
      nativeName lineTo script(
        "double" lineTo script(),
        "is" lineTo script("less" lineTo script("than" lineTo script("double"))))
    DoubleMinusDoubleNative ->
      nativeName lineTo script(
        "double" lineTo script(),
        "minus" lineTo script("double"))
    DoublePlusDoubleNative ->
      nativeName lineTo script(
        "double" lineTo script(),
        "plus" lineTo script("double"))
    DoubleTimesDoubleNative ->
      nativeName lineTo script(
        "double" lineTo script(),
        "times" lineTo script("double"))
    DoubleStringNative ->
      nativeName lineTo script(
        "string" lineTo script("double"))
    ObjectEqualsObjectNative ->
      nativeName lineTo script(
        "object" lineTo script(),
        "equals" lineTo script("object"))
    StringLengthNative ->
      nativeName lineTo script(
        "length" lineTo script("string"))
    StringPlusStringNative ->
      nativeName lineTo script(
        "string" lineTo script(),
        "plus" lineTo script("string"))
  }
