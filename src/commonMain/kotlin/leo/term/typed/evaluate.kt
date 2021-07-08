package leo.term.typed

import leo.term.Evaluator
import leo.term.value

fun <V> Evaluator<V>.typedValue(typed: TypedTerm<V>): TypedValue<V> =
  typed(value(typed.v), typed.t)

fun <V> TypedTerm<V>.typedValue(evaluator: Evaluator<V>): TypedValue<V> =
  evaluator.typedValue(this)