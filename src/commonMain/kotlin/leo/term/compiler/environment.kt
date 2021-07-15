package leo.term.compiler

import leo.Literal
import leo.Script
import leo.ScriptLine
import leo.isEmpty
import leo.lineTo
import leo.plus
import leo.script
import leo.term.compiled.Compiled
import leo.term.compiled.CompiledLine
import leo.term.compiled.Line
import leo.term.compiled.compiled
import leo.term.compiled.infix
import leo.typeLine
import leo.typeName

data class Environment<V>(
  val literalFn: (Literal) -> Line<V>,
  val resolveOrNullFn: (Compiled<V>) -> Compiled<V>?,
  val scriptLineFn: (V) -> ScriptLine)

fun <V> Environment<V>.compiled(script: Script): Compiled<V> =
  context.module.compiled(script)

fun <V> Environment<V>.compiledLine(literal: Literal): CompiledLine<V> =
  compiled(literalFn(literal), literal.typeLine)

fun <V> Environment<V>.resolveTypeOrNull(compiled: Compiled<V>): Compiled<V>? =
  compiled.infix(typeName) { lhs, rhs ->
    if (!lhs.type.isEmpty) compileError(
      lhs.type.script
        .plus("type" lineTo rhs.type.script)
        .plus("is" lineTo script(
          "not" lineTo script(
            "matching" lineTo script(
              "type" lineTo script(
                "any" lineTo script(
                  "compiled")))))))
    else resolveType(rhs)
  }

fun <V> Script.compiled(environment: Environment<V>): Compiled<V> =
  environment.compiled(this)