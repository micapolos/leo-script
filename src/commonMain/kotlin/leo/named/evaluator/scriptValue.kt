package leo.named.evaluator

import leo.Script
import leo.get
import leo.named.compiler.expression
import leo.named.value.Value

val Script.value: Value get() =
	expression.valueEvaluation.get(dictionary())
