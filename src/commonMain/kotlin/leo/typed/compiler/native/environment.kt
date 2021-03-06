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
      { compiled -> compiled.resolveOrNull },
      { it.scriptLine },
      { nativeTypesEnvironment },
      { typeLine -> typeLine.nativeScriptLineOrNull })

val Compiled<Native>.resolveOrNull: Compiled<Native>? get() =
  when (type) {
    type(nativeNumberTypeLine, "plus" lineTo nativeNumberType) ->
      nativeCompiled(DoublePlusDoubleNative, type functionLineTo nativeNumberType).invoke(this)
    type(nativeNumberTypeLine, "minus" lineTo nativeNumberType) ->
      nativeCompiled(DoubleMinusDoubleNative, type functionLineTo nativeNumberType).invoke(this)
    type(nativeNumberTypeLine, "times" lineTo nativeNumberType) ->
      nativeCompiled(DoubleTimesDoubleNative, type functionLineTo nativeNumberType).invoke(this)
    type(nativeNumberTypeLine, "divided" lineTo type("by" lineTo nativeNumberType)) ->
      nativeCompiled(DoubleDividedByDoubleNative, type functionLineTo nativeNumberType).invoke(this)
    type(numberName lineTo type("pi")) ->
      nativeCompiled(PiDoubleNative, nativeNumberTypeLine)
    type(numberName lineTo type("e")) ->
      nativeCompiled(EDoubleNative, nativeNumberTypeLine)
    type("root" lineTo nativeNumberType) ->
      nativeCompiled(DoubleRootNative, type functionLineTo nativeNumberType).invoke(this)
    type("sinus" lineTo nativeNumberType) ->
      nativeCompiled(DoubleSinusNative, type functionLineTo nativeNumberType).invoke(this)
    type("cosinus" lineTo nativeNumberType) ->
      nativeCompiled(DoubleCosinusNative, type functionLineTo nativeNumberType).invoke(this)
    type(nativeNumberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (nativeNumberType)))) ->
      nativeCompiled(DoubleIsLessThanDoubleNative, type functionLineTo isType).invoke(this)
    type(nativeTextTypeLine, "plus" lineTo nativeTextType) ->
      nativeCompiled(StringPlusStringNative, type functionLineTo nativeTextType).invoke(this)
    type("length" lineTo nativeTextType) ->
      nativeCompiled(StringLengthNative, type functionLineTo type("length" lineTo nativeNumberType)).invoke(this)
    type(textName lineTo nativeNumberType) ->
      nativeCompiled(DoubleStringNative, type functionLineTo nativeTextType).invoke(this)
    else -> infix(isName) { isLhs, isRhs ->
      isRhs.prefix(equalName) { isEqualRhs ->
        isEqualRhs.prefix(toName) { isEqualToRhs ->
          compiled(isLhs.onlyCompiledLine).as_(compiled(isEqualToRhs.onlyCompiledLine).type).let {
            nativeCompiled(ObjectEqualsObjectNative, type functionLineTo isType).invoke(this)
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