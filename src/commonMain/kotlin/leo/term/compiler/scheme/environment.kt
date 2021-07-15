package leo.term.compiler.scheme

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral
import leo.TypeLine
import leo.atom
import leo.functionLineTo
import leo.isType
import leo.line
import leo.lineTo
import leo.native
import leo.numberName
import leo.numberType
import leo.numberTypeLine
import leo.primitive
import leo.script
import leo.term.compiled.Compiled
import leo.term.compiled.compiled
import leo.term.compiled.invoke
import leo.term.compiled.nativeCompiled
import leo.term.compiled.nativeLine
import leo.term.compiler.Environment
import leo.textName
import leo.textType
import leo.textTypeLine
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
    type(numberTypeLine, "plus" lineTo numberType) ->
      nativeCompiled(scheme("+"), type(type functionLineTo numberType))
    type(numberTypeLine, "minus" lineTo numberType) ->
      nativeCompiled(scheme("-"), type(type functionLineTo numberType))
    type(numberTypeLine, "times" lineTo numberType) ->
      nativeCompiled(scheme("*"), type(type functionLineTo numberType))
    type(numberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (numberType)))) ->
      nativeCompiled(scheme("<"), type(type functionLineTo isType))
    type(textTypeLine, "plus" lineTo textType) ->
      nativeCompiled(scheme("string-append"), type(type functionLineTo textType))
    type("length" lineTo textType) ->
      nativeCompiled(scheme("string-length"), type(type functionLineTo type("length" lineTo numberType)))
    type(numberTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo numberType))) ->
      nativeCompiled(scheme("="), type(type functionLineTo isType))
    type(textTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo textType))) ->
      nativeCompiled(scheme("="), type(type functionLineTo isType))
    else -> null
  }

val Literal.scheme: Scheme get() =
  scheme(toString())

val Literal.schemeTypeLine: TypeLine
  get() =
    when (this) {
      is NumberLiteral -> numberName lineTo type(line(atom(primitive(native(script("number"))))))
      is StringLiteral -> textName lineTo type(line(atom(primitive(native(script("string"))))))
    }