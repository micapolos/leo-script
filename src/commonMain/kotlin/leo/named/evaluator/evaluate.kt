package leo.named.evaluator

import leo.get
import leo.named.expression.Expression
import leo.named.value.Value

val Expression.value: Value
	get() =
		dictionary().valueEvaluation(this).get(Unit)

val Expression.dictionary: Dictionary
	get() =
		dictionary().dictionaryEvaluation(this).get(Unit)

val Expression.module: Module
	get() =
		dictionary().moduleEvaluation(this).get(Unit)

fun Dictionary.value(expression: Expression): Value =
	valueEvaluation(expression).get(Unit)