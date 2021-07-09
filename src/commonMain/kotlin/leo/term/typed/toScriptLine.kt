package leo.term.typed

import leo.ScriptLine
import leo.lineTo
import leo.script
import leo.scriptLine
import leo.term.scriptLine

fun <V> TypedTerm<V>.toScriptLine(fn: (V) -> ScriptLine): ScriptLine =
  "compiled" lineTo script(
    v.scriptLine(fn),
    t.scriptLine)