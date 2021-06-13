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
import leo.type

data class Dictionary(val definitionStack: Stack<Definition>)

fun dictionary(): Dictionary = Dictionary(stack())

fun Dictionary.plus(definition: Definition): Dictionary =
	definitionStack.push(definition).let(::Dictionary)

fun Dictionary.plus(dictionary: Dictionary): Dictionary =
	fold(dictionary.definitionStack.reverse) { plus(it) }

fun Dictionary.value(body: Body): Value =
	when (body) {
		is ExpressionBody -> value(body.expression)
		is FnBody -> body.valueFn(this)
	}

fun Dictionary.value(expression: Expression): Value =
	expression.valueEvaluation.get(this)

fun Dictionary.value(typeStructure: Type): Value =
	definitionStack
		.mapFirst { valueLineOrNull(typeStructure) }
		.notNullOrError("$this.value($this)")

fun Dictionary.get(name: String): Value =
	value(type(name))

val Value.dictionary: Dictionary get() =
	lineStack.map { definition }.let(::Dictionary)