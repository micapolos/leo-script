package leo.indexed.evaluator

import leo.indexed.Expression

val Expression<Value>.evaluate: Value get() =
	context().evaluate(this)
