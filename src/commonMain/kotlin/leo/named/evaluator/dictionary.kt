package leo.named.evaluator

import leo.Stack
import leo.TypeStructure
import leo.base.notNullOrError
import leo.get
import leo.mapFirst
import leo.named.Expression
import leo.push

data class Dictionary<out T>(val definitionStack: Stack<Definition<T>>)

fun <T> Dictionary<T>.plus(definition: Definition<T>): Dictionary<T> =
	definitionStack.push(definition).let(::Dictionary)

fun <T> Dictionary<T>.value(expression: Expression<T>): Value<T> =
	expression.valueEvaluation.get(this)

fun <T> Dictionary<T>.value(typeStructure: TypeStructure): Value<T> =
	definitionStack
		.mapFirst { valueOrNull(typeStructure) }
		.notNullOrError("$this.value($this)")