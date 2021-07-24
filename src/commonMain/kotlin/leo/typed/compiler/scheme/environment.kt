package leo.typed.compiler.scheme

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
import leo.typed.compiler.python.scriptLineOrNull
import scheme.Scheme
import scheme.scheme
import kotlin.math.E
import kotlin.math.PI

val schemeEnvironment: Environment<Scheme>
  get() =
    Environment(
      { literal -> compiled(nativeLine(literal.scheme), literal.schemeTypeLine) },
      { compiled -> compiled.resolveOrNull },
      { it.leoScriptLine },
      { schemeTypesEnvironment },
      { typeLine -> typeLine.scriptLineOrNull })

val Compiled<Scheme>.resolveOrNull: Compiled<Scheme>? get() =
  when (type) {
    type(schemeNumberTypeLine, "plus" lineTo schemeNumberType) ->
      nativeCompiled(scheme("+"), type functionLineTo schemeNumberType).invoke(this)
    type(schemeNumberTypeLine, "minus" lineTo schemeNumberType) ->
      nativeCompiled(scheme("-"), type functionLineTo schemeNumberType).invoke(this)
    type(schemeNumberTypeLine, "times" lineTo schemeNumberType) ->
      nativeCompiled(scheme("*"), type functionLineTo schemeNumberType).invoke(this)
    type(schemeNumberTypeLine, "divided" lineTo type("by" lineTo schemeNumberType)) ->
      nativeCompiled(scheme("/"), type functionLineTo schemeNumberType).invoke(this)
    type(numberName lineTo type("pi")) ->
      nativeCompiled(scheme("$PI"), schemeNumberTypeLine)
    type(numberName lineTo type("e")) ->
      nativeCompiled(scheme("$E"), schemeNumberTypeLine)
    type("root" lineTo schemeNumberType) ->
      nativeCompiled(scheme("sqrt"), type functionLineTo schemeNumberType).invoke(this)
    type("sinus" lineTo schemeNumberType) ->
      nativeCompiled(scheme("sin"), type functionLineTo schemeNumberType).invoke(this)
    type("cosinus" lineTo schemeNumberType) ->
      nativeCompiled(scheme("cos"), type functionLineTo schemeNumberType).invoke(this)
    type(schemeNumberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (schemeNumberType)))) ->
      nativeCompiled(scheme("<"), type functionLineTo isType).invoke(this)
    type(textName lineTo schemeNumberType) ->
      nativeCompiled(scheme("number->string"), type functionLineTo schemeTextType).invoke(this)
    type(schemeTextTypeLine, "plus" lineTo schemeTextType) ->
      nativeCompiled(scheme("string-append"), type functionLineTo schemeTextType).invoke(this)
    type("length" lineTo schemeTextType) ->
      nativeCompiled(scheme("string-length"), type functionLineTo type("length" lineTo schemeNumberType)).invoke(this)
    else ->
      infix(isName) { isLhs, isRhs ->
        isRhs.prefix(equalName) { isEqualRhs ->
          isEqualRhs.prefix(toName) { isEqualToRhs ->
            compiled(isLhs.onlyCompiledLine).as_(compiled(isEqualToRhs.onlyCompiledLine).type).let {
              nativeCompiled(scheme("equal?"), type functionLineTo isType).invoke(this)
            }
          }
        }
      }
  }

val Literal.scheme: Scheme get() =
  scheme(toString())

val Literal.schemeTypeLine: TypeLine
  get() =
    when (this) {
      is NumberLiteral -> schemeNumberTypeLine
      is StringLiteral -> schemeTextTypeLine
    }