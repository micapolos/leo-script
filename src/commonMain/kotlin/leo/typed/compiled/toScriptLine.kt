package leo.typed.compiled

import leo.Empty
import leo.IndexVariable
import leo.Script
import leo.ScriptLine
import leo.functionName
import leo.lineTo
import leo.literal
import leo.map
import leo.natives.fieldName
import leo.plus
import leo.script
import leo.scriptLine
import leo.yesNoName

fun <V> Compiled<V>.toScriptLine(fn: (V) -> ScriptLine): ScriptLine =
  "compiled" lineTo script(
    expression.toScriptLine(fn),
    type.scriptLine)

fun <V> Expression<V>.toScriptLine(fn: (V) -> ScriptLine): ScriptLine =
  "expression" lineTo toScript(fn)

fun <V> Expression<V>.toScript(fn: (V) -> ScriptLine): Script =
  when (this) {
    is ApplyExpression -> apply.toScript(fn)
    is SelectExpression -> select.toScript(fn)
    is SwitchExpression -> switch.toScript(fn)
    is ContentExpression -> content.toScript(fn)
//    is BindExpression -> bind.toScript(fn)
    is VariableExpression -> variable.toScript(fn)
    is LinkExpression -> link.toScript(fn)
    is EmptyExpression -> empty.toScript(fn)
  }

@Suppress("unused")
fun <V> Empty.toScript(@Suppress("UNUSED_PARAMETER") fn: (V) -> ScriptLine): Script =
  script()

fun <V> Apply<V>.toScript(fn: (V) -> ScriptLine): Script =
  script(lhs.toScriptLine(fn))
    .plus("apply" lineTo script(rhs.toScriptLine(fn)))

fun <V> Select<V>.toScript(fn: (V) -> ScriptLine): Script =
  choice.script()
    .plus(
      "case" lineTo script(case.line.toScriptLine(fn)))

fun <V> Switch<V>.toScript(fn: (V) -> ScriptLine): Script =
  script(lhs.toScriptLine(fn))
    .plus("switch" lineTo caseStack.map { toScriptLine(fn) }.script)

fun <V> Link<V>.toScript(fn: (V) -> ScriptLine): Script =
  lhsCompiled.expression.toScript(fn).plus(rhsCompiledLine.line.toScriptLine(fn))

fun <V> Content<V>.toScript(fn: (V) -> ScriptLine): Script =
  script("content" lineTo script(lhs.toScriptLine(fn)))

fun <V> Line<V>.toScriptLine(fn: (V) -> ScriptLine): ScriptLine =
  when (this) {
    is FieldLine -> field.toScriptLine(fn)
    is FunctionLine -> function.toScriptLine(fn)
    is GetLine -> get.toScriptLine(fn)
    is NativeLine -> fn(native)
  }

fun <V> Field<V>.toScriptLine(fn: (V) -> ScriptLine): ScriptLine =
  fieldName lineTo script(
    "name" lineTo script(name),
    rhs.toScriptLine(fn))

fun <V> Function<V>.toScriptLine(fn: (V) -> ScriptLine): ScriptLine =
  functionName lineTo script(
    "parameter" lineTo script(paramType.scriptLine),
    body.toScriptLine(fn))

fun <V> Get<V>.toScriptLine(@Suppress("UNUSED_PARAMETER") fn: (V) -> ScriptLine): ScriptLine =
  "get" lineTo script(
    lhs.toScriptLine(fn),
    "index" lineTo script(literal(index)))

fun <V> Body<V>.toScriptLine(fn: (V) -> ScriptLine): ScriptLine =
  "body" lineTo script(
    compiled.toScriptLine(fn),
    "recursive" lineTo script(isRecursive.yesNoName))

fun <V> IndexVariable.toScript(@Suppress("UNUSED_PARAMETER") fn: (V) -> ScriptLine): Script =
  script(toScriptLine)

val IndexVariable.toScriptLine: ScriptLine get() =
  "variable" lineTo script(literal(index))