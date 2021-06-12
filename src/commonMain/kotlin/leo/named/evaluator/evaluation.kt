package leo.named.evaluator

import leo.Literal
import leo.Stateful
import leo.bind
import leo.flat
import leo.getStateful
import leo.map
import leo.named.expression.AnyExpression
import leo.named.expression.Expression
import leo.named.expression.Field
import leo.named.expression.FieldExpression
import leo.named.expression.Function
import leo.named.expression.FunctionExpression
import leo.named.expression.Get
import leo.named.expression.GetExpression
import leo.named.expression.Invoke
import leo.named.expression.InvokeExpression
import leo.named.expression.LiteralExpression
import leo.named.expression.Switch
import leo.named.expression.SwitchExpression
import leo.named.expression.Variable
import leo.named.expression.VariableExpression
import leo.named.expression.expression
import leo.named.value.FunctionValue
import leo.named.value.Structure
import leo.named.value.Value
import leo.named.value.anyValue
import leo.named.value.get
import leo.named.value.name
import leo.named.value.value
import leo.named.value.valueTo
import leo.setStateful
import leo.stateful
import leo.updateStateful

typealias Evaluation<T, V> = Stateful<Dictionary<T>, V>
fun <T, V> V.evaluation(): Evaluation<T, V> = stateful()

fun <T> dictionaryEvaluation(): Evaluation<T, Dictionary<T>> = getStateful()
fun <T> Dictionary<T>.setEvaluation(): Evaluation<T, Unit> = setStateful(this)
fun <T> updateDictionaryEvaluation(fn: (Dictionary<T>) -> Dictionary<T>): Evaluation<T, Unit> = updateStateful(fn)

val <T> leo.named.expression.Structure<T>.structureEvaluation: Evaluation<T, Structure<T>> get() =
	expressionStack.map { valueEvaluation }.flat.map(::Structure)

fun <T> Dictionary<T>.valueEvaluation(expression: Expression<T>): Evaluation<T, Value<T>> =
	TODO()

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
	value(this).evaluation()

val <T> Get<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	lhs.valueEvaluation.map { it.get(name) }

val <T> Invoke<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	function.valueEvaluation.bind { functionValue ->
		(functionValue as FunctionValue).function.let { function ->
			params.structureEvaluation.bind { paramsStructure ->
				dictionaryEvaluation<T>().map { dictionary ->
					dictionary
						.plus(paramsStructure.dictionary)
						.value(function.bodyExpression)
				}
			}
		}
	}

fun <T> Literal.valueEvaluation(): Evaluation<T, Value<T>> =
	value<T>(this).evaluation()

val <T> Switch<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	lhs.valueEvaluation.bind { value ->
		dictionaryEvaluation<T>().map { dictionary ->
			dictionary.plus(value.definition).value(expression(value.name))
		}
	}

fun <T> Variable.valueEvaluation(): Evaluation<T, Value<T>> =
	dictionaryEvaluation<T>().map { it.value(typeStructure) }
