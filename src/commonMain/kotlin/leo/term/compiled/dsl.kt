package leo.term.compiled

import leo.anyName
import leo.base.orIfNull
import leo.functionName
import leo.functionOrNull
import leo.isName
import leo.lineTo
import leo.notName
import leo.plus
import leo.script
import leo.term.compiler.compileError

fun <V> Compiled<V>.apply(compiled: Compiled<V>): Compiled<V> =
  compiled.type.functionOrNull
    .orIfNull {
      compileError(
        compiled.type.script
        .plus(isName lineTo script(
          notName lineTo script(
            anyName lineTo script(
              functionName))))) }
    .let { function ->
      if (type != function.lhsType)
        compileError(
          compiled.type.script
            .plus(isName lineTo script(
              notName lineTo function.lhsType.script)))
      else
        compiled(
          expression(apply(this, compiled)),
          function.rhsType)
    }
