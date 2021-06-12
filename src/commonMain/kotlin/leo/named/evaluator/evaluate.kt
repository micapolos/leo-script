package leo.named.evaluator

import leo.get
import leo.named.expression.Line
import leo.named.value.Value

val <T> Line<T>.evaluate: Value<T> get() =
	valueEvaluation.get(dictionary())