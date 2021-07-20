package leo.typed.compiler.python

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral
import leo.TypeLine
import leo.functionLineTo
import leo.isType
import leo.lineTo
import leo.script
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiled.compiled
import leo.typed.compiled.invoke
import leo.typed.compiled.nativeCompiled
import leo.typed.compiled.nativeLine
import leo.typed.compiler.Environment
import leo.typed.compiler.compileError

val pythonEnvironment: Environment<Python>
  get() =
    Environment(
      { literal -> compiled(nativeLine(literal.python), literal.pythonTypeLine) },
      { compiled -> compiled.resolveOrNull?.invoke(compiled) },
      { compileError(script("script")) },
      { pythonTypesEnvironment })

val Compiled<Python>.resolveOrNull: Compiled<Python>? get() =
  when (type) {
    type(pythonNumberTypeLine, "plus" lineTo pythonNumberType) ->
      nativeCompiled(python("operator.add"), type(type functionLineTo pythonNumberType))
    type(pythonNumberTypeLine, "minus" lineTo pythonNumberType) ->
      nativeCompiled(python("operator.sub"), type(type functionLineTo pythonNumberType))
    type(pythonNumberTypeLine, "times" lineTo pythonNumberType) ->
      nativeCompiled(python("operator.mul"), type(type functionLineTo pythonNumberType))
    type(pythonNumberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (pythonNumberType)))) ->
      nativeCompiled(python("operator.lt"), type(type functionLineTo isType))
    type(pythonTextTypeLine, "plus" lineTo pythonTextType) ->
      nativeCompiled(python("operator.add"), type(type functionLineTo pythonTextType))
    type("length" lineTo pythonTextType) ->
      nativeCompiled(python("len"), type(type functionLineTo type("length" lineTo pythonNumberType)))
    type(pythonNumberTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo pythonNumberType))) ->
      nativeCompiled(python("operator.eq"), type(type functionLineTo isType))
    type(pythonTextTypeLine, "is" lineTo type("equal" lineTo type("to" lineTo pythonTextType))) ->
      nativeCompiled(python("operator.eq"), type(type functionLineTo isType))
    else -> null
  }

val Literal.python: Python
  get() =
  python(toString())

val Literal.pythonTypeLine: TypeLine
  get() =
    when (this) {
      is NumberLiteral -> pythonNumberTypeLine
      is StringLiteral -> pythonTextTypeLine
    }