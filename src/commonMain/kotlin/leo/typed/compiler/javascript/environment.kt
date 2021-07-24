package leo.typed.compiler.javascript

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

val javascriptEnvironment: Environment<Javascript>
  get() =
    Environment(
      { literal -> compiled(nativeLine(literal.javascript), literal.javascriptTypeLine) },
      { compiled -> compiled.resolveOrNull },
      { compileError(script("script")) },
      { javascriptTypesEnvironment },
      { typeLine -> typeLine.scriptLineOrNull })

val Compiled<Javascript>.resolveOrNull: Compiled<Javascript>? get() =
  when (type) {
    type(javascriptNumberTypeLine, "plus" lineTo javascriptNumberType) ->
      nativeCompiled(javascript("((x,y)=>x+y)"), type functionLineTo javascriptNumberType).invoke(this)
    type(javascriptNumberTypeLine, "minus" lineTo javascriptNumberType) ->
      nativeCompiled(javascript("((x,y)=>x-y)"), type functionLineTo javascriptNumberType).invoke(this)
    type(javascriptNumberTypeLine, "times" lineTo javascriptNumberType) ->
      nativeCompiled(javascript("((x,y)=>x*y)"), type functionLineTo javascriptNumberType).invoke(this)
    type(javascriptNumberTypeLine, "divided" lineTo type("by" lineTo javascriptNumberType)) ->
      nativeCompiled(javascript("((x,y)=>x/y)"), type functionLineTo javascriptNumberType).invoke(this)
    type(numberName lineTo type("pi")) ->
      nativeCompiled(javascript("Math.PI"), javascriptNumberTypeLine)
    type(numberName lineTo type("e")) ->
      nativeCompiled(javascript("Math.E"), javascriptNumberTypeLine)
    type("root" lineTo javascriptNumberType) ->
      nativeCompiled(javascript("Math.sqrt"), type functionLineTo javascriptNumberType).invoke(this)
    type("sinus" lineTo javascriptNumberType) ->
      nativeCompiled(javascript("Math.sin"), type functionLineTo javascriptNumberType).invoke(this)
    type("cosinus" lineTo javascriptNumberType) ->
      nativeCompiled(javascript("Math.cos"), type functionLineTo javascriptNumberType).invoke(this)
    type(javascriptNumberTypeLine, "is" lineTo type("less" lineTo type("than" lineTo (javascriptNumberType)))) ->
      nativeCompiled(javascript("((x,y)=>x<y)"), type functionLineTo isType).invoke(this)
    type(javascriptTextTypeLine, "plus" lineTo javascriptTextType) ->
      nativeCompiled(javascript("((x,y)=>x+y)"), type functionLineTo javascriptTextType).invoke(this)
    type(textName lineTo javascriptNumberType) ->
      nativeCompiled(javascript("String"), type functionLineTo javascriptTextType).invoke(this)
    type("length" lineTo javascriptTextType) ->
      nativeCompiled(javascript("(s=>s.length)"), type functionLineTo type("length" lineTo javascriptNumberType)).invoke(this)
    else ->
      infix(isName) { isLhs, isRhs ->
        isRhs.prefix(equalName) { isEqualRhs ->
          isEqualRhs.prefix(toName) { isEqualToRhs ->
            compiled(isLhs.onlyCompiledLine).as_(compiled(isEqualToRhs.onlyCompiledLine).type).let {
              nativeCompiled(javascript("((x,y)=>x==y)"), type functionLineTo isType).invoke(this)
            }
          }
        }
      }
  }

val Literal.javascript: Javascript
  get() =
    when (this) {
      is NumberLiteral -> javascript(number.toString())
      is StringLiteral -> stringJavascript(string)
    }

val Literal.javascriptTypeLine: TypeLine
  get() =
    when (this) {
      is NumberLiteral -> javascriptNumberTypeLine
      is StringLiteral -> javascriptTextTypeLine
    }