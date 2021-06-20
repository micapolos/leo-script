package leo.named.evaluator

import leo.named.value.Value
import leo.named.value.value

data class Evaluator(
	val module: Module,
	val value: Value) { override fun toString() = scriptLine.toString() }

fun Module.evaluator(value: Value) = Evaluator(this, value)
val Module.evaluator get() = evaluator(value())

fun Evaluator.set(value: Value): Evaluator = copy(value = value)
fun Evaluator.set(module: Module): Evaluator = copy(module = module)

val Evaluator.dictionary: Dictionary get() = module.private.dictionary