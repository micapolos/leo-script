package leo.named.evaluator

import leo.get
import leo.named.expression.Line
import leo.named.value.ValueLine

val <T> Line<T>.evaluate: ValueLine<T>
	get() =
	lineEvaluation.get(dictionary())