package leo.term.indexed.scheme

import leo.Empty
import leo.IndexVariable
import leo.base.iterate
import leo.term.compiled.Scope
import leo.term.compiled.push
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
import scheme.listScheme
import scheme.nilScheme
import scheme.pair
import scheme.pairFirst
import scheme.pairSecond
import scheme.scheme
import scheme.vectorRef

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
    is VariableExpression -> variable.scheme
  }

val Empty.scheme: Scheme get() =
  scheme("`()")

fun ExpressionInvoke<Scheme>.scheme(scope: Scope): Scheme =
  scheme(lhs.scheme(scope), *params.map { scheme(scope) }.toTypedArray())

fun ExpressionFunction<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("lambda"),
    scheme(*0.until(arity).map { variable(scope.depth + it).scheme }.toTypedArray()),
    expression.scheme(scope.iterate(arity) { push }))

fun ExpressionRecursive<Scheme>.scheme(scope: Scope): Scheme =
  TODO()

fun ExpressionTuple<Scheme>.scheme(scope: Scope): Scheme =
  listScheme(*expressionList.map { it.scheme(scope) }.toTypedArray())

fun ExpressionGet<Scheme>.scheme(scope: Scope): Scheme =
  lhs.scheme(scope).vectorRef(scheme(index))

fun ExpressionIndexed<Scheme>.scheme(scope: Scope): Scheme =
  pair(scheme(index), expression.scheme(scope))

fun ExpressionSwitch<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("let"),
    scheme(
      scheme(scheme("idx"), lhs.scheme(scope)),
      scheme(variable(scope.depth).scheme, scheme("x").pairSecond)),
    scheme("idx").indexSwitch(*cases.map { scheme(scope.push) }.toTypedArray()))

fun ExpressionIndexedSwitch<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("let"),
    scheme(
      scheme(scheme("x"), lhs.scheme(scope)),
      scheme(scheme("idx"), scheme("x").pairFirst),
      scheme(variable(scope.depth).scheme, nilScheme),
      scheme("idx").indexSwitch(*cases.map { scheme(scope.push) }.toTypedArray())))

val IndexVariable.scheme get() = Scheme("v${index}")