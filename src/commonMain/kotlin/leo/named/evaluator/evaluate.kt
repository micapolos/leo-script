package leo.named.evaluator

import leo.get
import leo.named.expression.Expression
import leo.named.value.Value

val Expression.evaluate: Value
	get() =
		valueEvaluation.get(dictionary())