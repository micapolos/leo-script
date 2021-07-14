package leo.term.compiler

import leo.Script
import leo.ScriptField
import leo.TypeLine
import leo.dropName
import leo.line
import leo.lineTo
import leo.onlyLineOrNull
import leo.pickName
import leo.script
import leo.term.compiled.CompiledLine
import leo.term.compiled.CompiledSelect
import leo.term.compiled.onlyCompiledLineOrNull

data class SelectCompiler<V>(
  val context: Context<V>,
  val compiledSelect: CompiledSelect<V>)

fun <V> SelectCompiler<V>.plus(scriptField: ScriptField): SelectCompiler<V> =
  null
    ?: plusOrNull(scriptField)
    ?: compileError(script("select" lineTo script(line(scriptField))))

fun <V> SelectCompiler<V>.plusOrNull(scriptField: ScriptField): SelectCompiler<V>? =
  when (scriptField.name) {
    pickName -> pick(scriptField.rhs)
    dropName -> drop(scriptField.rhs)
    else -> null
  }

fun <V> SelectCompiler<V>.pick(script: Script): SelectCompiler<V> =
  context.compiled(script).onlyCompiledLineOrNull
    ?.let { pick(it) }
    ?: compileError(script(pickName lineTo script))

fun <V> SelectCompiler<V>.drop(script: Script): SelectCompiler<V> =
  context.type(script).onlyLineOrNull
    ?.let { drop(it) }
    ?: compileError(script(dropName lineTo script))

fun <V> SelectCompiler<V>.pick(compiledLine: CompiledLine<V>): SelectCompiler<V> =
  TODO()

fun <V> SelectCompiler<V>.drop(typeLine: TypeLine): SelectCompiler<V> =
  TODO()

