package leo.term.indexed.scheme

import leo.Empty
import leo.IndexVariable
import leo.base.iterate
import leo.term.compiled.Scope
import leo.term.compiled.push
import leo.term.indexed.EmptyExpression
import leo.term.indexed.Expression
import leo.term.indexed.Function
import leo.term.indexed.FunctionExpression
import leo.term.indexed.Get
import leo.term.indexed.GetExpression
import leo.term.indexed.IndexExpression
import leo.term.indexed.IndexSwitch
import leo.term.indexed.IndexSwitchExpression
import leo.term.indexed.Indexed
import leo.term.indexed.IndexedExpression
import leo.term.indexed.IndexedSwitch
import leo.term.indexed.IndexedSwitchExpression
import leo.term.indexed.Invoke
import leo.term.indexed.InvokeExpression
import leo.term.indexed.NativeExpression
import leo.term.indexed.Recursive
import leo.term.indexed.RecursiveExpression
import leo.term.indexed.Tuple
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

fun Invoke<Scheme>.scheme(scope: Scope): Scheme =
  scheme(lhs.scheme(scope), *params.map { scheme(scope) }.toTypedArray())

fun Function<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("lambda"),
    scheme(*0.until(arity).map { variable(scope.depth + it).scheme }.toTypedArray()),
    expression.scheme(scope.iterate(arity) { push }))

fun Recursive<Scheme>.scheme(scope: Scope): Scheme =
  TODO()

fun Tuple<Scheme>.scheme(scope: Scope): Scheme =
  listScheme(*list.map { it.scheme(scope) }.toTypedArray())

fun Get<Scheme>.scheme(scope: Scope): Scheme =
  lhs.scheme(scope).vectorRef(scheme(index))

fun Indexed<Scheme>.scheme(scope: Scope): Scheme =
  pair(scheme(index), expression.scheme(scope))

fun IndexSwitch<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("let"),
    scheme(
      scheme(scheme("idx"), lhs.scheme(scope)),
      scheme(variable(scope.depth).scheme, scheme("x").pairSecond)),
    scheme("idx").indexSwitch(*cases.map { scheme(scope.push) }.toTypedArray()))

fun IndexedSwitch<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("let"),
    scheme(
      scheme(scheme("x"), lhs.scheme(scope)),
      scheme(scheme("idx"), scheme("x").pairFirst),
      scheme(variable(scope.depth).scheme, nilScheme),
      scheme("idx").indexSwitch(*cases.map { scheme(scope.push) }.toTypedArray())))

val IndexVariable.scheme get() = Scheme("v${index}")