package leo.typed.compiler.native

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral
import leo.TypeLine
import leo.equalName
import leo.functionLineTo
import leo.isName
import leo.isType
import leo.lineTo
import leo.numberName
import leo.textName
import leo.toName
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiled.as_
import leo.typed.compiled.compiled
import leo.typed.compiled.infix
import leo.typed.compiled.invoke
import leo.typed.compiled.nativeCompiled
import leo.typed.compiled.nativeLine
import leo.typed.compiled.onlyCompiledLine
import leo.typed.compiled.prefix
import leo.typed.compiler.Environment

val nativeEnvironment: Environment<Native>
  get() =
    Environment(
      { literal -> compiled(nativeLine(literal.native), literal.nativeTypeLine) },
      { compiled -> compiled.resolveOrNull?.invoke(compiled) },
      { it.scriptLine },
      { nativeTypesEnvironment },
      { typeLine -> typeLine.nativeScriptLineOrNull })

val Compiled<Native>.resolveOrNull: Compiled<Native>? get() =
  when (type) {
    type(nativeNumberTypeLine, "plus" lineTo nativeNumberType) ->
      nativeCompiled(DoublePlusDoubleNative, type(type functionLineTo nativeNumberType))
    type(nativeNumberTypeLine, "minus" lineTo nativeNumberType) ->
      nativeCompiled(DoubleMinusDoubleNative, type(type functionLineTo nativeNumberType))
    type(nativeNumberTypeLine, "times" lineTo nativeNumberType) ->
      nativeCompiled(DoubleTimesDoubleNative, type(type functionLineTo nativeNumberType))
    type(nativeNumberTypeLine, "divided" lineTo type("by" lineTo nativeNumberType)) ->
      nativeCompiled(DoubleDividedByDoubleNative, type(type functionLineTo nativeNumberType))
    type(numberName lineTo type("pi")) ->
      nativeCompiled(PiDoubleNative, type(type functionLineTo nativeNumberType))
    type(nativeNumberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (nativeNumberType)))) ->
      nativeCompiled(DoubleIsLessThanDoubleNative, type(type functionLineTo isType))
    type(nativeTextTypeLine, "plus" lineTo nativeTextType) ->
      nativeCompiled(StringPlusStringNative, type(type functionLineTo nativeTextType))
    type("length" lineTo nativeTextType) ->
      nativeCompiled(StringLengthNative, type(type functionLineTo type("length" lineTo nativeNumberType)))
    type(textName lineTo nativeNumberType) ->
      nativeCompiled(DoubleStringNative, type(type functionLineTo nativeTextType))
    else -> infix(isName) { isLhs, isRhs ->
      isRhs.prefix(equalName) { isEqualRhs ->
        isEqualRhs.prefix(toName) { isEqualToRhs ->
          compiled(isLhs.onlyCompiledLine).as_(compiled(isEqualToRhs.onlyCompiledLine).type).let {
            nativeCompiled(ObjectEqualsObjectNative, type(type functionLineTo isType))
          }
        }
      }
    }
  }

val Literal.nativeTypeLine: TypeLine get() =
  when (this) {
    is NumberLiteral -> nativeNumberTypeLine
    is StringLiteral -> nativeTextTypeLine
  }