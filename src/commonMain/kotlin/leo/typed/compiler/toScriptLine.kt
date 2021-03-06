package leo.typed.compiler

import leo.ScriptLine
import leo.beingName
import leo.lineTo
import leo.listScriptLine
import leo.optionScriptLine
import leo.plus
import leo.script
import leo.scriptLine
import leo.typed.compiled.toScriptLine

val <V> Environment<V>.toScriptLine: ScriptLine get() =
  "environment" lineTo script("native")

val <V> Compiler<V>.toScriptLine: ScriptLine get() =
  "compiler" lineTo script(
    block.toScriptLine,
    compiled.toScriptLine(environment.scriptLineFn))

val <V> Block<V>.toScriptLine: ScriptLine get() =
  "block" lineTo script(
    module.toScriptLine,
    "param" lineTo compiledStack.listScriptLine { toScriptLine(context.environment.scriptLineFn) }.script)

val <V> Module<V>.toScriptLine: ScriptLine get() =
  "module" lineTo script(
    context.toScriptLine,
    "types" lineTo script(
      "block" lineTo script(
        typesBlockOrNull.optionScriptLine { toScriptLine })))

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
      is FunctionBinding -> function.scriptLine()
      is ConstantBinding -> constant.scriptLine
      is GivenBinding -> given.scriptLine
    })

val Constant.scriptLine: ScriptLine get() =
  "constant" lineTo lhsType.script.plus(beingName lineTo rhsType.script)

val TypeLineGiven.scriptLine: ScriptLine get() =
  "given" lineTo script(typeLine.scriptLine)
