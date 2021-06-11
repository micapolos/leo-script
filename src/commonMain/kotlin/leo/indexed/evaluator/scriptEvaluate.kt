package leo.indexed.evaluator

import leo.Script
import leo.indexed.compiler.typed

val Script.evaluate: Value get() =
	typed.expression.evaluate