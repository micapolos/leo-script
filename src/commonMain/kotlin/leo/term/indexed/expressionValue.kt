package leo.term.indexed

import leo.get

fun <V> Expression<V>.value(evaluator: Evaluator<V>): Value<V> =
  valueEvaluation(scope()).get(evaluator)