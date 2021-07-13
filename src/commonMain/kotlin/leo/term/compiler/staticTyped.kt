package leo.term.compiler

import leo.FieldScriptLine
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.base.fold
import leo.base.reverse
import leo.lineSeq
import leo.script
import leo.term.compiled.Compiled
import leo.term.compiled.CompiledLine
import leo.term.compiled.compiled
import leo.term.compiled.lineTo
import leo.term.compiled.plus
import leo.typeLine

fun <V> Environment<V>.resolveType(compiled: Compiled<V>): Compiled<V> =
  staticCompiled(compiled.type.script)

fun <V> Environment<V>.staticCompiled(script: Script): Compiled<V> =
  compiled<V>().fold(script.lineSeq.reverse) { plus(staticTypedLine(it)) }

fun <V> Environment<V>.staticTypedLine(scriptLine: ScriptLine): CompiledLine<V> =
  when (scriptLine) {
    is FieldScriptLine -> staticTypedLine(scriptLine.field)
    is LiteralScriptLine -> compiled(literalFn(scriptLine.literal), scriptLine.literal.typeLine)
  }

fun <V> Environment<V>.staticTypedLine(scriptField: ScriptField): CompiledLine<V> =
  scriptField.name lineTo staticCompiled(scriptField.rhs)