package leo.typed.compiler.python

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
import leo.script
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
import leo.typed.compiler.compileError

val pythonEnvironment: Environment<Python>
  get() =
    Environment(
      { literal -> compiled(nativeLine(literal.python), literal.pythonTypeLine) },
      { compiled -> compiled.resolveOrNull?.invoke(compiled) },
      { compileError(script("script")) },
      { pythonTypesEnvironment },
      { typeLine -> typeLine.scriptLineOrNull })

val Compiled<Python>.resolveOrNull: Compiled<Python>? get() =
  when (type) {
    type(pythonNumberTypeLine, "plus" lineTo pythonNumberType) ->
      nativeCompiled(python("operator.add"), type(type functionLineTo pythonNumberType))
    type(pythonNumberTypeLine, "minus" lineTo pythonNumberType) ->
      nativeCompiled(python("operator.sub"), type(type functionLineTo pythonNumberType))
    type(pythonNumberTypeLine, "times" lineTo pythonNumberType) ->
      nativeCompiled(python("operator.mul"), type(type functionLineTo pythonNumberType))
    type(pythonNumberTypeLine, "divided" lineTo type("by" lineTo pythonNumberType)) ->
      nativeCompiled(python("operator.div"), type(type functionLineTo pythonNumberType))
    type(numberName lineTo type("pi")) ->
      nativeCompiled(python("(lambda x: math.pi)"), type(type functionLineTo pythonNumberType))
    type(numberName lineTo type("e")) ->
      nativeCompiled(python("(lambda x: math.e)"), type(type functionLineTo pythonNumberType))
    type("root" lineTo pythonNumberType) ->
      nativeCompiled(python("math.sqrt"), type(type functionLineTo pythonNumberType))
    type("sinus" lineTo pythonNumberType) ->
      nativeCompiled(python("math.sin"), type(type functionLineTo pythonNumberType))
    type("cosinus" lineTo pythonNumberType) ->
      nativeCompiled(python("math.cos"), type(type functionLineTo pythonNumberType))
    type(pythonNumberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (pythonNumberType)))) ->
      nativeCompiled(python("operator.lt"), type(type functionLineTo isType))
    type(pythonTextTypeLine, "plus" lineTo pythonTextType) ->
      nativeCompiled(python("operator.add"), type(type functionLineTo pythonTextType))
    type(textName lineTo pythonNumberType) ->
      nativeCompiled(python("str"), type(type functionLineTo pythonTextType))
    type("length" lineTo pythonTextType) ->
      nativeCompiled(python("len"), type(type functionLineTo type("length" lineTo pythonNumberType)))
    else ->
      infix(isName) { isLhs, isRhs ->
        isRhs.prefix(equalName) { isEqualRhs ->
          isEqualRhs.prefix(toName) { isEqualToRhs ->
            compiled(isLhs.onlyCompiledLine).as_(compiled(isEqualToRhs.onlyCompiledLine).type).let {
              nativeCompiled(python("operator.eq"), type(type functionLineTo isType))
            }
          }
        }
      }
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