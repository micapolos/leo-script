package leo.named.evaluator

import leo.Script
import leo.get
import leo.named.compiler.expression
import leo.named.compiler.line
import leo.named.value.Value
import leo.named.value.ValueLine

val Script.value: Value<Unit> get() =
	expression.valueEvaluation.get(dictionary())

val Script.valueLine: ValueLine<Unit> get() =
	line.lineEvaluation.get(dictionary())
