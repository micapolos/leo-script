package leo.named.evaluator

import leo.Stack
import leo.Type
import leo.base.notNullOrError
import leo.fold
import leo.get
import leo.map
import leo.mapFirst
import leo.named.expression.Body
import leo.named.expression.Expression
import leo.named.expression.ExpressionBody
import leo.named.expression.FnBody
import leo.named.value.Value
import leo.push
import leo.reverse
import leo.stack

data class Dictionary<out T>(val definitionStack: Stack<Definition<T>>)

fun <T> dictionary(): Dictionary<T> = Dictionary(stack())

fun <T> Dictionary<T>.plus(definition: Definition<T>): Dictionary<T> =
	definitionStack.push(definition).let(::Dictionary)

fun <T> Dictionary<T>.plus(dictionary: Dictionary<T>): Dictionary<T> =
	fold(dictionary.definitionStack.reverse) { plus(it) }

fun <T> Dictionary<T>.value(body: Body<T>): Value<T> =
	when (body) {
		is ExpressionBody -> value(body.expression)
		is FnBody -> body.valueFn(this)
	}

fun <T> Dictionary<T>.value(expression: Expression<T>): Value<T> =
	expression.valueEvaluation.get(this)

fun <T> Dictionary<T>.value(typeStructure: Type): Value<T> =
	definitionStack
		.mapFirst { valueLineOrNull(typeStructure) }
		.notNullOrError("$this.value($this)")

val <T> Value<T>.dictionary: Dictionary<T> get() =
	lineStack.map { definition }.let(::Dictionary)