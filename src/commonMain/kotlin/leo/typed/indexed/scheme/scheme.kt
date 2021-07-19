package leo.typed.indexed.scheme

import leo.Empty
import leo.IndexVariable
import leo.base.iterate
import leo.typed.compiled.Scope
import leo.typed.compiled.push
import leo.typed.compiled.scope
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
import leo.variable
import scheme.Scheme
import scheme.scheme
import scheme.switch
import scheme.vectorRef
import scheme.vectorScheme

val Expression<Scheme>.scheme: Scheme get() =
  scheme(scope())

fun Expression<Scheme>.scheme(scope: Scope): Scheme =
  when (this) {
    is EmptyExpression -> scheme("`()")
    is InvokeExpression -> invoke.scheme(scope)
    is FunctionExpression -> function.scheme(scope)
    is RecursiveExpression -> recursive.scheme(scope)
    is GetExpression -> get.scheme(scope)
    is IndexExpression -> index.scheme(scope)
    is SwitchExpression -> switch.scheme(scope)
    is TupleExpression -> tuple.scheme(scope)
    is NativeExpression -> native
    is VariableExpression -> variable.scheme(scope)
    is BooleanExpression -> boolean.scheme
    is ConditionalExpression -> conditional.scheme(scope)
  }

@Suppress("unused")
val Empty.scheme: Scheme get() =
  scheme("`()")

fun ExpressionInvoke<Scheme>.scheme(scope: Scope): Scheme =
  scheme(lhs.scheme(scope), *params.map { it.scheme(scope) }.toTypedArray())

fun ExpressionFunction<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("lambda"),
    scheme(*0.until(arity).map { variable(scope.depth + it).scheme }.toTypedArray()),
    expression.scheme(scope.iterate(arity) { push }))

fun ExpressionRecursive<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("letrec"),
    scheme(scheme(variable(scope.depth).scheme, function.scheme(scope.push))),
    variable(scope.depth).scheme)

fun ExpressionTuple<Scheme>.scheme(scope: Scope): Scheme =
  vectorScheme(*expressionList.map { it.scheme(scope) }.toTypedArray())

fun ExpressionGet<Scheme>.scheme(scope: Scope): Scheme =
  lhs.scheme(scope).vectorRef(scheme(index))

fun Int.scheme(@Suppress("UNUSED_PARAMETER") scope: Scope): Scheme =
  scheme(this)

fun ExpressionConditional<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("if"),
    condition.scheme(scope),
    trueCase.scheme(scope),
    falseCase.scheme(scope))

fun ExpressionSwitch<Scheme>.scheme(scope: Scope): Scheme =
  lhs.scheme(scope).switch(*cases.map { it.scheme(scope) }.toTypedArray())

fun IndexVariable.scheme(scope: Scope) =
  variable(scope.depth - index - 1).scheme

val IndexVariable.scheme get() = Scheme("v${index}")