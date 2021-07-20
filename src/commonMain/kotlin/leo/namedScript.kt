package leo

fun <V> Named<V>.script(fn: (V) -> ScriptLine): Script =
  lineStack.map { scriptLine(fn) }.script

fun <V> NamedLine<V>.scriptLine(fn: (V) -> ScriptLine): ScriptLine =
  when (this) {
    is AnyNamedLine -> fn(any)
    is FieldNamedLine -> field.name lineTo field.rhs.script(fn)
  }

val <V> Named<V>.reflectScriptLine: ScriptLine get() =
  "named" lineTo script { it.anyScriptLine }

val <V> NamedLine<V>.reflectScriptLine: ScriptLine get() =
  "line" lineTo script("named" lineTo script(scriptLine { it.anyScriptLine }))

val Named<Literal>.literalScript: Script get() =
  script { line(it) }

val NamedLine<Literal>.literalScriptLine: ScriptLine get() =
  scriptLine { line(it) }
