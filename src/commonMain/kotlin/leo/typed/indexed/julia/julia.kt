package leo.typed.indexed.julia

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
import leo.typed.compiler.julia.Julia
import leo.typed.compiler.julia.get
import leo.typed.compiler.julia.ifThenElse
import leo.typed.compiler.julia.invoke
import leo.typed.compiler.julia.julia
import leo.typed.compiler.julia.tupleJulia
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

val Expression<Julia>.julia: Julia
  get() =
    julia(scope())

fun Expression<Julia>.julia(scope: Scope): Julia =
  when (this) {
    is EmptyExpression -> empty.julia
    is InvokeExpression -> invoke.julia(scope)
    is FunctionExpression -> function.julia(scope)
    is RecursiveExpression -> recursive.julia(scope)
    is GetExpression -> get.julia(scope)
    is IndexExpression -> index.julia(scope)
    is SwitchExpression -> switch.julia(scope)
    is TupleExpression -> tuple.julia(scope)
    is NativeExpression -> native
    is VariableExpression -> variable.julia(scope)
    is BooleanExpression -> boolean.julia(scope)
    is ConditionalExpression -> conditional.julia(scope)
  }

@Suppress("unused")
val Empty.julia: Julia
  get() =
    julia("()")

fun ExpressionInvoke<Julia>.julia(scope: Scope): Julia =
  when (lhs) {
    nativeExpression(julia("(+)")) ->
      julia("(${paramStack.getFromBottom(0)!!.julia(scope).string}+${paramStack.getFromBottom(1)!!.julia(scope).string})")
    nativeExpression(julia("(-)")) ->
      julia("(${paramStack.getFromBottom(0)!!.julia(scope).string}-${paramStack.getFromBottom(1)!!.julia(scope).string})")
    nativeExpression(julia("(*)")) ->
      julia("(${paramStack.getFromBottom(0)!!.julia(scope).string}*${paramStack.getFromBottom(1)!!.julia(scope).string})")
    nativeExpression(julia("(/)")) ->
      julia("(${paramStack.getFromBottom(0)!!.julia(scope).string}/${paramStack.getFromBottom(1)!!.julia(scope).string})")
    nativeExpression(julia("(==)")) ->
      julia("(${paramStack.getFromBottom(0)!!.julia(scope).string}==${paramStack.getFromBottom(1)!!.julia(scope).string})")
    nativeExpression(julia("(<)")) ->
      julia("(${paramStack.getFromBottom(0)!!.julia(scope).string}<${paramStack.getFromBottom(1)!!.julia(scope).string})")
    else -> lhs.julia(scope).invoke(*paramStack.map { julia(scope) }.array)
  }

fun ExpressionFunction<Julia>.julia(scope: Scope): Julia =
  julia(
    string(
      "((",
      0.until(arity).map { variable(scope.depth + it).julia.string }.joinToString(","),
      ")->",
      expression.julia(scope.iterate(arity) { push }).string,
      ")"))

fun ExpressionRecursive<Julia>.julia(scope: Scope): Julia =
  julia(
    string(
      "(function ",
      variable(scope.depth).julia.string,
      "(",
      0.until(function.arity).map { variable(scope.depth + it + 1).julia.string }.joinToString(","),
      ") return ",
      function.expression.julia(scope.push.iterate(function.arity) { push }).string,
      " end)"))

fun ExpressionTuple<Julia>.julia(scope: Scope): Julia =
  tupleJulia(*expressionStack.map { julia(scope) }.array)

fun ExpressionGet<Julia>.julia(scope: Scope): Julia =
  lhs.julia(scope).get(julia(index))

fun Boolean.julia(@Suppress("UNUSED_PARAMETER") scope: Scope): Julia =
  julia(this)

fun Int.julia(@Suppress("UNUSED_PARAMETER") scope: Scope): Julia =
  julia(this.inc())

fun ExpressionConditional<Julia>.julia(scope: Scope): Julia =
  condition.julia(scope).ifThenElse(trueCase.julia(scope), falseCase.julia(scope))

fun ExpressionSwitch<Julia>.julia(scope: Scope): Julia =
  tupleJulia(*caseStack.map { julia(scope) }.array)
    .get(lhs.julia(scope))
    .invoke()

fun IndexVariable.julia(scope: Scope) =
  variable(scope.depth - index - 1).julia

val IndexVariable.julia get() = Julia("v${index}")
