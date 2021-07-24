package leo.typed.indexed.javascript

import leo.Empty
import leo.IndexVariable
import leo.array
import leo.base.iterate
import leo.base.string
import leo.getFromBottom
import leo.map
import leo.typed.compiled.Scope
import leo.typed.compiled.push
import leo.typed.compiled.scope
import leo.typed.compiler.javascript.Javascript
import leo.typed.compiler.javascript.arrayJavascript
import leo.typed.compiler.javascript.get
import leo.typed.compiler.javascript.ifThenElse
import leo.typed.compiler.javascript.invoke
import leo.typed.compiler.javascript.javascript
import leo.typed.indexed.BooleanExpression
import leo.typed.indexed.ConditionalExpression
import leo.typed.indexed.EmptyExpression
import leo.typed.indexed.Expression
import leo.typed.indexed.ExpressionConditional
import leo.typed.indexed.ExpressionFunction
import leo.typed.indexed.ExpressionGet
import leo.typed.indexed.ExpressionInvoke
import leo.typed.indexed.ExpressionRecursive
import leo.typed.indexed.ExpressionSwitch
import leo.typed.indexed.ExpressionTuple
import leo.typed.indexed.FunctionExpression
import leo.typed.indexed.GetExpression
import leo.typed.indexed.IndexExpression
import leo.typed.indexed.InvokeExpression
import leo.typed.indexed.NativeExpression
import leo.typed.indexed.RecursiveExpression
import leo.typed.indexed.SwitchExpression
import leo.typed.indexed.TupleExpression
import leo.typed.indexed.VariableExpression
import leo.typed.indexed.nativeExpression
import leo.variable


val Expression<Javascript>.javascript: Javascript
  get() =
    javascript(scope())

fun Expression<Javascript>.javascript(scope: Scope): Javascript =
  when (this) {
    is EmptyExpression -> empty.javascript
    is InvokeExpression -> invoke.javascript(scope)
    is FunctionExpression -> function.javascript(scope)
    is RecursiveExpression -> recursive.javascript(scope)
    is GetExpression -> get.javascript(scope)
    is IndexExpression -> index.javascript(scope)
    is SwitchExpression -> switch.javascript(scope)
    is TupleExpression -> tuple.javascript(scope)
    is NativeExpression -> native
    is VariableExpression -> variable.javascript(scope)
    is BooleanExpression -> boolean.javascript(scope)
    is ConditionalExpression -> conditional.javascript(scope)
  }

@Suppress("unused")
val Empty.javascript: Javascript
  get() =
    javascript("null")

fun ExpressionInvoke<Javascript>.javascript(scope: Scope): Javascript =
  when (lhs) {
    nativeExpression(javascript("((x,y)=>x+y)")) ->
      javascript("(${paramStack.getFromBottom(0)!!.javascript(scope).string}+${paramStack.getFromBottom(1)!!.javascript(scope).string})")
    nativeExpression(javascript("((x,y)=>x-y)")) ->
      javascript("(${paramStack.getFromBottom(0)!!.javascript(scope).string}-${paramStack.getFromBottom(1)!!.javascript(scope).string})")
    nativeExpression(javascript("((x,y)=>x*y)")) ->
      javascript("(${paramStack.getFromBottom(0)!!.javascript(scope).string}*${paramStack.getFromBottom(1)!!.javascript(scope).string})")
    nativeExpression(javascript("((x,y)=>x/y)")) ->
      javascript("(${paramStack.getFromBottom(0)!!.javascript(scope).string}/${paramStack.getFromBottom(1)!!.javascript(scope).string})")
    nativeExpression(javascript("((x,y)=>x==y)")) ->
      javascript("(${paramStack.getFromBottom(0)!!.javascript(scope).string}==${paramStack.getFromBottom(1)!!.javascript(scope).string})")
    nativeExpression(javascript("((x,y)=>x<y)")) ->
      javascript("(${paramStack.getFromBottom(0)!!.javascript(scope).string}<${paramStack.getFromBottom(1)!!.javascript(scope).string})")
    nativeExpression(javascript("(s=>s.length)")) ->
      javascript("${paramStack.getFromBottom(0)!!.javascript(scope).string}.length")
    else -> lhs.javascript(scope).invoke(*paramStack.map { javascript(scope) }.array)
  }

fun ExpressionFunction<Javascript>.javascript(scope: Scope): Javascript =
  javascript(
    string(
      "((",
      0.until(arity).map { variable(scope.depth + it).javascript.string }.joinToString(","),
      ")=>",
      expression.javascript(scope.iterate(arity) { push }).string,
      ")"))

fun ExpressionRecursive<Javascript>.javascript(scope: Scope): Javascript =
  javascript(
    string(
      "(function ",
      variable(scope.depth).javascript.string,
      "(",
      0.until(function.arity).map { variable(scope.depth + it + 1).javascript.string }.joinToString(","),
      "){return ",
      function.expression.javascript(scope.push.iterate(function.arity) { push }).string,
      "})"))

fun ExpressionTuple<Javascript>.javascript(scope: Scope): Javascript =
  arrayJavascript(*expressionStack.map { javascript(scope) }.array)

fun ExpressionGet<Javascript>.javascript(scope: Scope): Javascript =
  lhs.javascript(scope).get(javascript(index))

fun Boolean.javascript(@Suppress("UNUSED_PARAMETER") scope: Scope): Javascript =
  javascript(this)

fun Int.javascript(@Suppress("UNUSED_PARAMETER") scope: Scope): Javascript =
  javascript(this)

fun ExpressionConditional<Javascript>.javascript(scope: Scope): Javascript =
  condition.javascript(scope).ifThenElse(trueCase.javascript(scope), falseCase.javascript(scope))

fun ExpressionSwitch<Javascript>.javascript(scope: Scope): Javascript =
  arrayJavascript(*caseStack.map { javascript(scope) }.array)
    .get(lhs.javascript(scope))
    .invoke()

fun IndexVariable.javascript(scope: Scope) =
  variable(scope.depth - index - 1).javascript

val IndexVariable.javascript get() = Javascript("v${index}")
