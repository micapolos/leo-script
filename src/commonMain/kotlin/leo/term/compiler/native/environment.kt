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
import leo.textType
import leo.textTypeLine
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
    type(numberTypeLine, "minus" lineTo numberType) ->
      nativeCompiled(DoubleMinusDoubleNative, type(type functionLineTo numberType))
    type(numberTypeLine, "times" lineTo numberType) ->
      nativeCompiled(DoubleTimesDoubleNative, type(type functionLineTo numberType))
    type(numberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (numberType)))) ->
      nativeCompiled(DoubleIsLessThanDoubleNative, type(type functionLineTo isType))
    type(textTypeLine, "plus" lineTo textType) ->
      nativeCompiled(StringPlusStringNative, type(type functionLineTo textType))
    type("length" lineTo textType) ->
      nativeCompiled(StringLengthNative, type(type functionLineTo type("length" lineTo numberType)))
    type(numberTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo numberType))) ->
      nativeCompiled(ObjectEqualsObjectNative, type(type functionLineTo isType))
    type(textTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo textType))) ->
      nativeCompiled(ObjectEqualsObjectNative, type(type functionLineTo isType))
    else -> null
  }