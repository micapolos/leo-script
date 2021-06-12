package leo.named.evaluator

import leo.Stateful
import leo.getStateful
import leo.map
import leo.named.AnyExpression
import leo.named.Expression
import leo.named.FieldExpression
import leo.named.FunctionExpression
import leo.named.GetExpression
import leo.named.InvokeExpression
import leo.named.LiteralExpression
import leo.named.SwitchExpression
import leo.named.VariableExpression
import leo.stateful

typealias Evaluation<T, V> = Stateful<Dictionary<T>, V>
fun <T, V> V.evaluation(): Evaluation<T, V> = stateful()

fun <T> dictionaryEvaluation(): Evaluation<T, Dictionary<T>> = getStateful()

val <T> leo.named.Structure<T>.structureEvaluation: Evaluation<T, Structure<T>> get() =
	TODO()

val <T> Expression<T>.evaluation: Evaluation<T, Value<T>> get() =
	when (this) {
		is AnyExpression -> any.anyValueEvaluation()
		is FieldExpression -> field.valueEvaluation()
		is FunctionExpression -> function.valueEvaluation()
		is GetExpression -> TODO()
		is InvokeExpression -> TODO()
		is LiteralExpression -> TODO()
		is SwitchExpression -> TODO()
		is VariableExpression -> TODO()
	}

fun <T> T.anyValueEvaluation(): Evaluation<T, Value<T>> =
	anyValue(this).evaluation()

fun <T> leo.named.Field<T>.valueEvaluation(): Evaluation<T, Value<T>> =
	rhs.structureEvaluation.map { name valueTo it }

fun <T> leo.named.Function<T>.valueEvaluation(): Evaluation<T, Value<T>> =
	TODO()