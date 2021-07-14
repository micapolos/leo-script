package leo.term.compiler.native

import leo.functionLineTo
import leo.isType
import leo.lineTo
import leo.numberType
import leo.numberTypeLine
import leo.term.compiled.Compiled
import leo.term.compiled.invoke
import leo.term.compiled.nativeCompiled
import leo.term.compiled.nativeLine
import leo.term.compiler.Environment
import leo.type

val nativeEnvironment: Environment<Native>
  get() =
    Environment(
      { literal -> nativeLine(literal.native) },
      { compiled -> compiled.resolveOrNull?.invoke(compiled) },
      { it.scriptLine })

val Compiled<Native>.resolveOrNull: Compiled<Native>? get() =
  when (type) {
    type(numberTypeLine, "plus" lineTo numberType) ->
      nativeCompiled(DoublePlusDoubleNative, type(type functionLineTo numberType))
    type(numberTypeLine, "equals" lineTo numberType) ->
      nativeCompiled(ObjectEqualsObjectNative, type(type functionLineTo isType))
    else -> null
  }