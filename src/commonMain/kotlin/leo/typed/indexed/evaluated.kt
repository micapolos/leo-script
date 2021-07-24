package leo.typed.indexed

import leo.array
import leo.get
import leo.mapIt
import leo.stack

data class Evaluated<V>(
  val evaluator: Evaluator<V>,
  val scope: ValueScope<V>,
  val value: Value<V>)

val <V> Evaluator<V>.evaluated: Evaluated<V> get() =
  Evaluated(this, scope(), value())

fun <V> Evaluated<V>.value(expression: Expression<V>): Value<V> =
  expression.valueEvaluation(scope).get(evaluator)

fun <V> Evaluated<V>.set(value: Value<V>): Evaluated<V> =
  copy(value = value)

fun <V> Evaluated<V>.set(expression: Expression<V>): Evaluated<V> =
  set(value(expression))

fun <V> Evaluated<V>.bind(expression: Expression<V>): Evaluated<V> =
  copy(scope = scope.plus(value(expression)))

fun <V> Evaluated<V>.ifThenElse(yesExpression: Expression<V>, noExpression: Expression<V>): Evaluated<V> =
  set(
    if (value.boolean) yesExpression
    else noExpression)

fun <V> Evaluated<V>.invoke(vararg expressions: Expression<V>): Evaluated<V> =
  invoke(*stack(*expressions).mapIt { value(it) }.array)

fun <V> Evaluated<V>.switch(vararg cases: Expression<V>): Evaluated<V> =
  set(cases[value.index])

fun <V> Evaluated<V>.invoke(vararg values: Value<V>): Evaluated<V> =
  set(value.invokeValueEvaluation(*values).get(evaluator))

fun <V> Evaluated<V>.get(index: Int): Evaluated<V> =
  set(value.get(index))
