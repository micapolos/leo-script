package leo.term.indexed

import leo.Empty
import leo.IndexVariable
import leo.Stateful
import leo.array
import leo.base.fold
import leo.base.reverseStack
import leo.base.seq
import leo.bind
import leo.flat
import leo.getStateful
import leo.map
import leo.stateful

typealias Evaluation<V, T> = Stateful<Evaluator<V>, T>
fun <V, T> T.evaluation(): Evaluation<V, T> = stateful()
fun <V> evaluatorEvaluation(): Evaluation<V, Evaluator<V>> = getStateful()

fun <V> Expression<V>.valueEvaluation(scope: ValueScope<V>): Evaluation<V, Value<V>> =
  when (this) {
    is EmptyExpression -> empty.valueEvaluation(scope)
    is FunctionExpression -> function.valueEvaluation(scope)
    is IndexExpression -> index.valueEvaluation(scope)
    is IndexSwitchExpression -> switch.valueEvaluation(scope)
    is IndexedExpression -> indexed.valueEvaluation(scope)
    is IndexedSwitchExpression -> switch.valueEvaluation(scope)
    is InvokeExpression -> invoke.valueEvaluation(scope)
    is NativeExpression -> native.nativeValueEvaluation(scope)
    is RecursiveExpression -> recursive.valueEvaluation(scope)
    is TupleExpression -> tuple.valueEvaluation(scope)
    is GetExpression -> get.valueEvaluation(scope)
    is VariableExpression -> variable.valueEvaluation(scope)
  }

@Suppress("unused")
fun <V> V.nativeValueEvaluation(@Suppress("UNUSED_PARAMETER") scope: ValueScope<V>): Evaluation<V, Value<V>> =
  nativeValue(this).evaluation()

@Suppress("unused")
fun <V> Empty.valueEvaluation(@Suppress("UNUSED_PARAMETER") scope: ValueScope<V>): Evaluation<V, Value<V>> =
  value<V>(this).evaluation()

fun <V> ExpressionTuple<V>.valueEvaluation(scope: ValueScope<V>): Evaluation<V, Value<V>> =
  expressionList.seq.reverseStack.map { valueEvaluation(scope) }.flat.map { valueStack ->
    value(*valueStack.array)
  }

fun <V> Int.valueEvaluation(@Suppress("UNUSED_PARAMETER") scope: ValueScope<V>): Evaluation<V, Value<V>> =
  value<V>(this).evaluation()

fun <V> ExpressionSwitch<V>.valueEvaluation(@Suppress("UNUSED_PARAMETER") scope: ValueScope<V>): Evaluation<V, Value<V>> =
  lhs.valueEvaluation(scope).bind { lhsValue ->
    cases[lhsValue.index].valueEvaluation(scope)
  }

fun <V> ExpressionIndexed<V>.valueEvaluation(@Suppress("UNUSED_PARAMETER") scope: ValueScope<V>): Evaluation<V, Value<V>> =
  expression.valueEvaluation(scope).map { value ->
    value(indexed(index, value))
  }

fun <V> ExpressionIndexedSwitch<V>.valueEvaluation(@Suppress("UNUSED_PARAMETER") scope: ValueScope<V>): Evaluation<V, Value<V>> =
  lhs.valueEvaluation(scope).bind { lhsValue ->
    lhsValue.indexed.let { indexed ->
      cases[indexed.index].valueEvaluation(scope.plus(indexed.value))
    }
  }

fun <V> ExpressionFunction<V>.valueEvaluation(scope: ValueScope<V>): Evaluation<V, Value<V>> =
  value(function(scope, expression)).evaluation()

fun <V> ExpressionRecursive<V>.valueEvaluation(scope: ValueScope<V>): Evaluation<V, Value<V>> =
  value(recursive(function(scope, function.expression))).evaluation()

fun <V> ExpressionGet<V>.valueEvaluation(scope: ValueScope<V>): Evaluation<V, Value<V>> =
  lhs.valueEvaluation(scope).map { it.get(index) }

fun <V> IndexVariable.valueEvaluation(scope: ValueScope<V>): Evaluation<V, Value<V>> =
  scope.value(this).evaluation()

fun <V> ExpressionInvoke<V>.valueEvaluation(scope: ValueScope<V>): Evaluation<V, Value<V>> =
  lhs.valueEvaluation(scope).bind { lhsValue ->
    params.seq.reverseStack.map { valueEvaluation(scope) }.flat.bind { paramStack ->
      lhsValue.invokeValueEvaluation(*paramStack.array)
    }
  }

fun <V> Value<V>.invokeValueEvaluation(vararg params: Value<V>): Evaluation<V, Value<V>> =
  when (this) {
    is FunctionValue -> function.invokeValueEvaluation(*params)
    is RecursiveValue -> recursive.invokeValueEvaluation(*params)
    is NativeValue -> evaluatorEvaluation<V>().map { it.invokeFn.invoke(native, params) }
    else -> null!!
  }

fun <V> ValueFunction<V>.invokeValueEvaluation(vararg params: Value<V>): Evaluation<V, Value<V>> =
  expression.valueEvaluation(scope.fold(params) { plus(it) })

fun <V> ValueRecursive<V>.invokeValueEvaluation(vararg params: Value<V>): Evaluation<V, Value<V>> =
  function.recursive.invokeValueEvaluation(*params)
