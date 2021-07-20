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
import scheme.Scheme
import scheme.scheme

val schemeEnvironment: Environment<Scheme>
  get() =
    Environment(
      { literal -> compiled(nativeLine(literal.scheme), literal.schemeTypeLine) },
      { compiled -> compiled.resolveOrNull?.invoke(compiled) },
      { it.leoScriptLine },
      { typesNativeEnvironment })

val Compiled<Scheme>.resolveOrNull: Compiled<Scheme>? get() =
  when (type) {
    type(schemeNumberTypeLine, "plus" lineTo schemeNumberType) ->
      nativeCompiled(scheme("+"), type(type functionLineTo schemeNumberType))
    type(schemeNumberTypeLine, "minus" lineTo schemeNumberType) ->
      nativeCompiled(scheme("-"), type(type functionLineTo schemeNumberType))
    type(schemeNumberTypeLine, "times" lineTo schemeNumberType) ->
      nativeCompiled(scheme("*"), type(type functionLineTo schemeNumberType))
    type(schemeNumberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (schemeNumberType)))) ->
      nativeCompiled(scheme("<"), type(type functionLineTo isType))
    type(schemeTextTypeLine, "plus" lineTo schemeTextType) ->
      nativeCompiled(scheme("string-append"), type(type functionLineTo schemeTextType))
    type("length" lineTo schemeTextType) ->
      nativeCompiled(scheme("string-length"), type(type functionLineTo type("length" lineTo schemeNumberType)))
    else ->
      infix(isName) { isLhs, isRhs ->
        isRhs.prefix(equalName) { isEqualRhs ->
          isEqualRhs.prefix(toName) { isEqualToRhs ->
            compiled(isLhs.onlyCompiledLine).as_(compiled(isEqualToRhs.onlyCompiledLine).type).let {
              nativeCompiled(scheme("equal?"), type(type functionLineTo isType))
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