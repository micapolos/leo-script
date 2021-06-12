package leo.named.evaluator

import leo.get
import leo.named.expression.Expression
import leo.named.value.Value

val <T> Expression<T>.evaluate: Value<T>
	get() =
		valueEvaluation.get(dictionary())