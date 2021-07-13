package leo.term.indexed.scheme

import leo.Empty
import leo.IndexVariable
import leo.base.iterate
import leo.term.compiled.Scope
import leo.term.compiled.push
import leo.term.compiled.scope
import leo.term.indexed.EmptyExpression
import leo.term.indexed.Expression
import leo.term.indexed.ExpressionFunction
import leo.term.indexed.ExpressionGet
import leo.term.indexed.ExpressionIndexed
import leo.term.indexed.ExpressionIndexedSwitch
import leo.term.indexed.ExpressionInvoke
import leo.term.indexed.ExpressionRecursive
import leo.term.indexed.ExpressionSwitch
import leo.term.indexed.ExpressionTuple
import leo.term.indexed.FunctionExpression
import leo.term.indexed.GetExpression
import leo.term.indexed.IndexExpression
import leo.term.indexed.IndexSwitchExpression
import leo.term.indexed.IndexedExpression
import leo.term.indexed.IndexedSwitchExpression
import leo.term.indexed.InvokeExpression
import leo.term.indexed.NativeExpression
import leo.term.indexed.RecursiveExpression
import leo.term.indexed.TupleExpression
import leo.term.indexed.VariableExpression
import leo.variable
import scheme.Scheme
import scheme.indexSwitch
import scheme.pair
import scheme.pairFirst
import scheme.pairSecond
import scheme.scheme
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
    is IndexExpression -> scheme(index)
    is IndexSwitchExpression -> switch.scheme(scope)
    is IndexedExpression -> indexed.scheme(scope)
    is IndexedSwitchExpression -> switch.scheme(scope)
    is TupleExpression -> tuple.scheme(scope)
    is NativeExpression -> native
    is VariableExpression -> variable.scheme(scope)
  }

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

fun ExpressionIndexed<Scheme>.scheme(scope: Scope): Scheme =
  pair(scheme(index), expression.scheme(scope))

fun ExpressionSwitch<Scheme>.scheme(scope: Scope): Scheme =
  lhs.scheme(scope).indexSwitch(*cases.map { it.scheme(scope) }.toTypedArray())

fun ExpressionIndexedSwitch<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("let*"),
    scheme(
      scheme(scheme("x"), lhs.scheme(scope)),
      scheme(scheme("i"), scheme("x").pairFirst),
      scheme(variable(scope.depth).scheme, scheme("x").pairSecond)),
    scheme("i").indexSwitch(*cases.map { it.scheme(scope.push) }.toTypedArray()))

fun IndexVariable.scheme(scope: Scope) =
  variable(scope.depth - index - 1).scheme

val IndexVariable.scheme get() = Scheme("v${index}")