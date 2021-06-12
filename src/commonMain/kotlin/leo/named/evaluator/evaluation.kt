package leo.named.evaluator

import leo.Literal
import leo.Stateful
import leo.bind
import leo.flat
import leo.getStateful
import leo.map
import leo.named.AnyExpression
import leo.named.Expression
import leo.named.Field
import leo.named.FieldExpression
import leo.named.Function
import leo.named.FunctionExpression
import leo.named.Get
import leo.named.GetExpression
import leo.named.Invoke
import leo.named.InvokeExpression
import leo.named.LiteralExpression
import leo.named.SwitchExpression
import leo.named.VariableExpression
import leo.setStateful
import leo.stateful
import leo.updateStateful

typealias Evaluation<T, V> = Stateful<Dictionary<T>, V>
fun <T, V> V.evaluation(): Evaluation<T, V> = stateful()

fun <T> dictionaryEvaluation(): Evaluation<T, Dictionary<T>> = getStateful()
fun <T> Dictionary<T>.setEvaluation(): Evaluation<T, Unit> = setStateful(this)
fun <T> updateDictionaryEvaluation(fn: (Dictionary<T>) -> Dictionary<T>): Evaluation<T, Unit> = updateStateful(fn)

val <T> leo.named.Structure<T>.structureEvaluation: Evaluation<T, Structure<T>> get() =
	expressionStack.map { valueEvaluation }.flat.map(::Structure)

val <T> Expression<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	when (this) {
		is AnyExpression -> any.anyValueEvaluation
		is FieldExpression -> field.valueEvaluation
		is FunctionExpression -> function.valueEvaluation
		is GetExpression -> get.valueEvaluation
		is InvokeExpression -> invoke.valueEvaluation
		is LiteralExpression -> literal.valueEvaluation()
		is SwitchExpression -> switch.valueEvaluation
		is VariableExpression -> variable.valueEvaluation()
	}

val <T> T.anyValueEvaluation: Evaluation<T, Value<T>> get() =
	anyValue(this).evaluation()

val <T> Field<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	structure.structureEvaluation.map { name valueTo it }

val <T> Function<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	value(body).evaluation()

val <T> Get<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	lhs.valueEvaluation.map { it.get(name) }

val <T> Invoke<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	function.valueEvaluation.bind { functionValue ->
		params.structureEvaluation.map { paramsStructure ->
			functionValue.invoke(paramsStructure)
		}
	}

fun <T> Literal.valueEvaluation(): Evaluation<T, Value<T>> =
	value<T>(this).evaluation()

val <T> leo.named.Switch<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	lhs.valueEvaluation.map { it.switch(cases) }

fun <T> leo.named.Variable.valueEvaluation(): Evaluation<T, Value<T>> =
	dictionaryEvaluation<T>().map { it.value(typeStructure) }
