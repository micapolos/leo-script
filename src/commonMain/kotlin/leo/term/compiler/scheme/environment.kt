package leo.term.compiler.scheme

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
import scheme.Scheme
import scheme.scheme

val schemeEnvironment: Environment<Scheme>
  get() =
    Environment(
      { literal -> compiled(nativeLine(literal.scheme), literal.schemeTypeLine) },
      { compiled -> compiled.resolveOrNull?.invoke(compiled) },
      { it.toScriptLine })

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
    type(schemeNumberTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo schemeNumberType))) ->
      nativeCompiled(scheme("="), type(type functionLineTo isType))
    type(schemeTextTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo schemeTextType))) ->
      nativeCompiled(scheme("="), type(type functionLineTo isType))
    else -> null
  }

val Literal.scheme: Scheme get() =
  scheme(toString())

val Literal.schemeTypeLine: TypeLine
  get() =
    when (this) {
      is NumberLiteral -> schemeNumberTypeLine
      is StringLiteral -> schemeTextTypeLine
    }