package leo.typed.compiler

import leo.FieldScriptLine
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.base.fold
import leo.base.reverse
import leo.lineSeq
import leo.script
import leo.typed.compiled.Compiled
import leo.typed.compiled.CompiledLine
import leo.typed.compiled.compiled
import leo.typed.compiled.lineTo
import leo.typed.compiled.plus

fun <V> Environment<V>.resolveType(compiled: Compiled<V>): Compiled<V> =
  staticCompiled(compiled.type.script)

fun <V> Environment<V>.staticCompiled(script: Script): Compiled<V> =
  compiled<V>().fold(script.lineSeq.reverse) { plus(staticTypedLine(it)) }

fun <V> Environment<V>.staticTypedLine(scriptLine: ScriptLine): CompiledLine<V> =
  when (scriptLine) {
    is FieldScriptLine -> staticTypedLine(scriptLine.field)
    is LiteralScriptLine -> literalFn(scriptLine.literal)
  }

fun <V> Environment<V>.staticTypedLine(scriptField: ScriptField): CompiledLine<V> =
  scriptField.name lineTo staticCompiled(scriptField.rhs)