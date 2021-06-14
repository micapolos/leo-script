package leo.named.evaluator

import leo.Script
import leo.named.compiler.typedExpression
import leo.named.library.preludeContext
import leo.named.value.Value

val Script.value: Value get() =
	typedExpression(preludeContext).expression.evaluate
