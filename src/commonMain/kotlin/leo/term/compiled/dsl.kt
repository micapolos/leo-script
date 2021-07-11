package leo.term.compiled

import leo.Type
import leo.applyName
import leo.base.fold
import leo.base.notNullIf
import leo.base.orIfNull
import leo.functionOrNull
import leo.lineTo
import leo.onlyOrNull
import leo.plus
import leo.push
import leo.script
import leo.structureOrNull
import leo.term.compiler.compileError
import leo.type

fun <V> compiled(vararg lines: CompiledLine<V>): Compiled<V> =
  Compiled(expression(tuple<V>()), type()).fold(lines) { plus(it) }

fun <V> nativeCompiled(native: V, type: Type): Compiled<V> =
  compiled(expression(tuple(nativeLine(native))), type)

val <V> Compiled<V>.compiledTuple: CompiledTuple<V> get() =
  type.structureOrNull.orIfNull { compileError(script("tuple")) }.let { structure ->
    if (expression is TupleExpression) CompiledTuple(expression.tuple, structure)
    else structure.lineStack.onlyOrNull
      ?.let { CompiledTuple(tuple(line(get(this, 0))), structure) }
      ?: TODO()
  }

fun <V> CompiledTuple<V>.plus(compiledLine: CompiledLine<V>): CompiledTuple<V> =
  compiled(Tuple(tuple.lineStack.push(compiledLine.line)), typeStructure.plus(compiledLine.typeLine))

val <V> CompiledTuple<V>.compiled: Compiled<V> get() =
  compiled(expression(tuple), type(typeStructure))

fun <V> Compiled<V>.plus(compiledLine: CompiledLine<V>): Compiled<V> =
  compiledTuple.plus(compiledLine).compiled

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
