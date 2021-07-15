package leo.term.compiler.native

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral
import leo.TypeLine
import leo.functionLineTo
import leo.isType
import leo.lineTo
import leo.term.compiled.Compiled
import leo.term.compiled.compiled
import leo.term.compiled.invoke
import leo.term.compiled.nativeCompiled
import leo.term.compiled.nativeLine
import leo.term.compiler.Environment
import leo.type

val nativeEnvironment: Environment<Native>
  get() =
    Environment(
      { literal -> compiled(nativeLine(literal.native), literal.nativeTypeLine) },
      { compiled -> compiled.resolveOrNull?.invoke(compiled) },
      { it.scriptLine })

val Compiled<Native>.resolveOrNull: Compiled<Native>? get() =
  when (type) {
    type(nativeNumberTypeLine, "plus" lineTo nativeNumberType) ->
      nativeCompiled(DoublePlusDoubleNative, type(type functionLineTo nativeNumberType))
    type(nativeNumberTypeLine, "minus" lineTo nativeNumberType) ->
      nativeCompiled(DoubleMinusDoubleNative, type(type functionLineTo nativeNumberType))
    type(nativeNumberTypeLine, "times" lineTo nativeNumberType) ->
      nativeCompiled(DoubleTimesDoubleNative, type(type functionLineTo nativeNumberType))
    type(nativeNumberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (nativeNumberType)))) ->
      nativeCompiled(DoubleIsLessThanDoubleNative, type(type functionLineTo isType))
    type(nativeTextTypeLine, "plus" lineTo nativeTextType) ->
      nativeCompiled(StringPlusStringNative, type(type functionLineTo nativeTextType))
    type("length" lineTo nativeTextType) ->
      nativeCompiled(StringLengthNative, type(type functionLineTo type("length" lineTo nativeNumberType)))
    type(nativeNumberTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo nativeNumberType))) ->
      nativeCompiled(ObjectEqualsObjectNative, type(type functionLineTo isType))
    type(nativeTextTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo nativeTextType))) ->
      nativeCompiled(ObjectEqualsObjectNative, type(type functionLineTo isType))
    else -> null
  }

val Literal.nativeTypeLine: TypeLine get() =
  when (this) {
    is NumberLiteral -> nativeNumberTypeLine
    is StringLiteral -> nativeTextTypeLine
  }