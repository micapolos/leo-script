package leo.term.indexed

import leo.Empty
import leo.IndexVariable
import leo.Script
import leo.ScriptLine
import leo.lineTo
import leo.listScriptLine
import leo.literal
import leo.plus
import leo.script
import leo.stack

typealias Fn<V> = (V) -> ScriptLine

fun <V> Expression<V>.script(fn: Fn<V>): Script =
  when (this) {
    is BooleanExpression -> boolean.script(fn)
    is ConditionalExpression -> conditional.script(fn)
    is EmptyExpression -> empty.script(fn)
    is FunctionExpression -> function.script(fn)
    is GetExpression -> get.script(fn)
    is IndexExpression -> index.script(fn)
    is InvokeExpression -> invoke.script(fn)
    is NativeExpression -> native.nativeScript(fn)
    is RecursiveExpression -> recursive.script(fn)
    is SwitchExpression -> switch.script(fn)
    is TupleExpression -> tuple.script(fn)
    is VariableExpression -> variable.script(fn)
  }

fun <V> Boolean.script(fn: Fn<V>): Script =
  script("boolean" lineTo script(if (this) "true" else "false"))

fun <V> ExpressionConditional<V>.script(fn: Fn<V>): Script =
  condition.script(fn)
    .plus(
      "switch" lineTo script(
        "true" lineTo trueCase.script(fn),
        "false" lineTo falseCase.script(fn)))

fun <V> Empty.script(fn: Fn<V>): Script = script()

fun <V> ExpressionFunction<V>.script(fn: Fn<V>): Script =
  script(
    "function" lineTo script(
      "arity" lineTo script(literal(arity)),
      "body" lineTo expression.script(fn)))

fun <V> ExpressionGet<V>.script(fn: Fn<V>): Script =
  lhs.script(fn).plus("get" lineTo script(literal(index)))

fun <V> Int.script(fn: Fn<V>): Script =
  script("index" lineTo script(literal(this)))

fun <V> ExpressionInvoke<V>.script(fn: Fn<V>): Script =
  lhs.script(fn)
    .plus("invoke" lineTo script(
      stack(*params.toTypedArray()).listScriptLine { "param" lineTo script(fn) }))

fun <V> V.nativeScript(fn: Fn<V>): Script =
  script("native" lineTo script(fn(this)))

fun <V> ExpressionRecursive<V>.script(fn: Fn<V>): Script =
  script("recursive" lineTo function.script(fn))

fun <V> ExpressionSwitch<V>.script(fn: Fn<V>): Script =
  lhs.script(fn)
    .plus(
      "switch" lineTo script(
        stack(*cases.toTypedArray()).listScriptLine { "case" lineTo script(fn) }))

fun <V> ExpressionTuple<V>.script(fn: Fn<V>): Script =
  script(
    "tuple" lineTo script(
      stack(*expressionList.toTypedArray()).listScriptLine { "item" lineTo script(fn) }))

fun <V> IndexVariable.script(fn: Fn<V>): Script =
  script("variable" lineTo script(literal(index)))