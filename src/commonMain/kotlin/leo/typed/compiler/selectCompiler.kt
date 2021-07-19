package leo.typed.compiler

import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.TypeLine
import leo.base.fold
import leo.base.reverse
import leo.dropName
import leo.fieldOrNull
import leo.lineSeq
import leo.lineTo
import leo.onlyLineOrNull
import leo.pickName
import leo.script
import leo.typed.compiled.CompiledLine
import leo.typed.compiled.CompiledSelect
import leo.typed.compiled.drop
import leo.typed.compiled.onlyCompiledLineOrNull
import leo.typed.compiled.pick

data class SelectCompiler<V>(
  val module: Module<V>,
  val compiledSelect: CompiledSelect<V>)

fun <V> SelectCompiler<V>.plus(script: Script): SelectCompiler<V> =
  fold(script.lineSeq.reverse) { plus(it) }

fun <V> SelectCompiler<V>.plus(scriptLine: ScriptLine): SelectCompiler<V> =
  null
    ?: plusOrNull(scriptLine)
    ?: compileError(script("select" lineTo script(scriptLine)))

fun <V> SelectCompiler<V>.plusOrNull(scriptLine: ScriptLine): SelectCompiler<V>? =
  scriptLine.fieldOrNull?.let { plusOrNull(it) }

fun <V> SelectCompiler<V>.plusOrNull(scriptField: ScriptField): SelectCompiler<V>? =
  when (scriptField.name) {
    pickName -> pick(scriptField.rhs)
    dropName -> drop(scriptField.rhs)
    else -> null
  }

fun <V> SelectCompiler<V>.pick(script: Script): SelectCompiler<V> =
  module.compiled(script).onlyCompiledLineOrNull
    ?.let { pick(it) }
    ?: compileError(script(pickName lineTo script))

fun <V> SelectCompiler<V>.drop(script: Script): SelectCompiler<V> =
  module.type(script).onlyLineOrNull
    ?.let { drop(it) }
    ?: compileError(script(dropName lineTo script))

fun <V> SelectCompiler<V>.pick(compiledLine: CompiledLine<V>): SelectCompiler<V> =
  copy(compiledSelect = compiledSelect.pick(compiledLine))

fun <V> SelectCompiler<V>.drop(typeLine: TypeLine): SelectCompiler<V> =
  copy(compiledSelect = compiledSelect.drop(typeLine))

