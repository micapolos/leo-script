package leo.typed.compiler

import leo.Literal
import leo.Script
import leo.ScriptLine
import leo.TypeLine
import leo.Types
import leo.isEmpty
import leo.lineTo
import leo.plus
import leo.script
import leo.typeName
import leo.typed.compiled.Compiled
import leo.typed.compiled.CompiledLine
import leo.typed.compiled.infix

data class Environment<V>(
  val literalFn: (Literal) -> CompiledLine<V>,
  val resolveOrNullFn: (Compiled<V>) -> Compiled<V>?,
  val scriptLineFn: (V) -> ScriptLine,
  val typesNativeEnvironmentFn: () -> Environment<Types>,
  val typeLineScriptLineOrNullFn: (TypeLine) -> ScriptLine?)

fun <V> Environment<V>.compiled(script: Script): Compiled<V> =
  context.module.compiled(script)

fun <V> Environment<V>.compiledLine(literal: Literal): CompiledLine<V> =
  literalFn(literal)

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
