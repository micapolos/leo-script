package leo.term.compiled

import leo.Type
import leo.applyName
import leo.base.notNullIf
import leo.functionOrNull
import leo.lineTo
import leo.plus
import leo.script
import leo.term.compiler.compileError

fun <V> nativeCompiled(native: V, type: Type): Compiled<V> =
  compiled(expression(tuple(nativeLine(native))), type)

fun <V> Compiled<V>.apply(compiled: Compiled<V>): Compiled<V> =
  compiled.type.functionOrNull
    ?.let { function ->
      notNullIf(type == function.lhsType) {
        compiled(
          expression(apply(this, compiled)),
          function.rhsType)
      }
    }
    ?: compileError(compiled.type.script.plus(applyName lineTo compiled.type.script))
