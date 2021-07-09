package leo.term.compiler

import leo.ScriptLine
import leo.lineTo
import leo.listScriptLine
import leo.script
import leo.scriptLine
import leo.term.scriptLine
import leo.term.typed.toScriptLine

val <V> Environment<V>.toScriptLine: ScriptLine get() =
  "environment" lineTo script("native")

val <V> Compiler<V>.toScriptLine: ScriptLine get() =
  "compiler" lineTo script(
    module.toScriptLine,
    typedTerm.toScriptLine(environment.scriptLineFn))

val <V> Module<V>.toScriptLine: ScriptLine get() =
  "module" lineTo script(
    context.toScriptLine,
    "term" lineTo termStack.listScriptLine { scriptLine(context.environment.scriptLineFn) }.script)

val <V> Context<V>.toScriptLine: ScriptLine get() =
  "context" lineTo script(
    environment.toScriptLine,
    scope.toScriptLine)

val Scope.toScriptLine: ScriptLine get() =
  "scope" lineTo script(
    "binding" lineTo bindingStack.listScriptLine { toScriptLine }.script)

val Binding.toScriptLine: ScriptLine get() =
  "binding" lineTo script(
    when (this) {
      is DefinitionBinding -> definition.toScriptLine
      is GivenBinding -> given.toScriptLine
    })

val Definition.toScriptLine: ScriptLine get() =
  "definition" lineTo script(function.scriptLine)

val Given.toScriptLine: ScriptLine get() =
  "given" lineTo script(type.scriptLine)