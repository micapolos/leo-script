package leo.named.evaluator

import leo.Stack
import leo.TypeStructure
import leo.base.notNullOrError
import leo.fold
import leo.get
import leo.map
import leo.mapFirst
import leo.named.expression.Line
import leo.named.value.Structure
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

fun <T> Dictionary<T>.value(line: Line<T>): Value<T> =
	line.valueEvaluation.get(this)

fun <T> Dictionary<T>.value(typeStructure: TypeStructure): Value<T> =
	definitionStack
		.mapFirst { valueOrNull(typeStructure) }
		.notNullOrError("$this.value($this)")

val <T> Structure<T>.dictionary: Dictionary<T> get() =
	valueStack.map { definition }.let(::Dictionary)