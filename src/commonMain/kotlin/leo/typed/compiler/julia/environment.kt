package leo.typed.compiler.julia

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral
import leo.TypeLine
import leo.equalName
import leo.functionLineTo
import leo.isName
import leo.isType
import leo.lineTo
import leo.literalString
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

val juliaEnvironment: Environment<Julia>
  get() =
    Environment(
      { literal -> compiled(nativeLine(literal.julia), literal.juliaTypeLine) },
      { compiled -> compiled.resolveOrNull },
      { compileError(script("script")) },
      { juliaTypesEnvironment },
      { typeLine -> typeLine.scriptLineOrNull })

val Compiled<Julia>.resolveOrNull: Compiled<Julia>? get() =
  when (type) {
    type(juliaNumberTypeLine, "plus" lineTo juliaNumberType) ->
      nativeCompiled(julia("(+)"), type functionLineTo juliaNumberType).invoke(this)
    type(juliaNumberTypeLine, "minus" lineTo juliaNumberType) ->
      nativeCompiled(julia("(-)"), type functionLineTo juliaNumberType).invoke(this)
    type(juliaNumberTypeLine, "times" lineTo juliaNumberType) ->
      nativeCompiled(julia("(*)"), type functionLineTo juliaNumberType).invoke(this)
    type(juliaNumberTypeLine, "divided" lineTo type("by" lineTo juliaNumberType)) ->
      nativeCompiled(julia("(/)"), type functionLineTo juliaNumberType).invoke(this)
    type(numberName lineTo type("pi")) ->
      nativeCompiled(julia("pi"), juliaNumberTypeLine)
    type(numberName lineTo type("e")) ->
      nativeCompiled(julia("exp(1)"), juliaNumberTypeLine)
    type("root" lineTo juliaNumberType) ->
      nativeCompiled(julia("sqrt"), type functionLineTo juliaNumberType).invoke(this)
    type("sinus" lineTo juliaNumberType) ->
      nativeCompiled(julia("sin"), type functionLineTo juliaNumberType).invoke(this)
    type("cosinus" lineTo juliaNumberType) ->
      nativeCompiled(julia("cos"), type functionLineTo juliaNumberType).invoke(this)
    type(juliaNumberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (juliaNumberType)))) ->
      nativeCompiled(julia("(<)"), type functionLineTo isType).invoke(this)
    type(juliaTextTypeLine, "plus" lineTo juliaTextType) ->
      nativeCompiled(julia("(+)"), type functionLineTo juliaTextType).invoke(this)
    type(textName lineTo juliaNumberType) ->
      nativeCompiled(julia("string"), type functionLineTo juliaTextType).invoke(this)
    type("length" lineTo juliaTextType) ->
      nativeCompiled(julia("length"), type functionLineTo type("length" lineTo juliaNumberType)).invoke(this)
    else ->
      infix(isName) { isLhs, isRhs ->
        isRhs.prefix(equalName) { isEqualRhs ->
          isEqualRhs.prefix(toName) { isEqualToRhs ->
            compiled(isLhs.onlyCompiledLine).as_(compiled(isEqualToRhs.onlyCompiledLine).type).let {
              nativeCompiled(julia("(==)"), type functionLineTo isType).invoke(this)
            }
          }
        }
      }
  }

val Literal.julia: Julia
  get() =
    when (this) {
      is NumberLiteral -> julia(number.toString())
      is StringLiteral -> julia(string.literalString)
    }

val Literal.juliaTypeLine: TypeLine
  get() =
    when (this) {
      is NumberLiteral -> juliaNumberTypeLine
      is StringLiteral -> juliaTextTypeLine
    }