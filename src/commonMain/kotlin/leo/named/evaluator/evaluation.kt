package leo.named.evaluator

import leo.Empty
import leo.Literal
import leo.Stateful
import leo.bind
import leo.getStateful
import leo.map
import leo.named.expression.AnyLine
import leo.named.expression.EmptyExpression
import leo.named.expression.Field
import leo.named.expression.FieldLine
import leo.named.expression.FunctionLine
import leo.named.expression.Get
import leo.named.expression.GetExpression
import leo.named.expression.Invoke
import leo.named.expression.InvokeExpression
import leo.named.expression.Line
import leo.named.expression.Link
import leo.named.expression.LinkExpression
import leo.named.expression.LiteralLine
import leo.named.expression.Switch
import leo.named.expression.SwitchExpression
import leo.named.expression.Variable
import leo.named.expression.VariableExpression
import leo.named.expression.expression
import leo.named.value.Value
import leo.named.value.ValueLine
import leo.named.value.anyValueLine
import leo.named.value.function
import leo.named.value.get
import leo.named.value.line
import leo.named.value.lineTo
import leo.named.value.name
import leo.named.value.plus
import leo.named.value.unsafeFunction
import leo.named.value.unsafeLine
import leo.named.value.value
import leo.named.value.valueLine
import leo.setStateful
import leo.stateful
import leo.updateStateful

typealias Evaluation<T, V> = Stateful<Dictionary<T>, V>
fun <T, V> V.evaluation(): Evaluation<T, V> = stateful()

fun <T> dictionaryEvaluation(): Evaluation<T, Dictionary<T>> = getStateful()
fun <T> Dictionary<T>.setEvaluation(): Evaluation<T, Unit> = setStateful(this)
fun <T> updateDictionaryEvaluation(fn: (Dictionary<T>) -> Dictionary<T>): Evaluation<T, Unit> = updateStateful(fn)

val <T> leo.named.expression.Expression<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	when (this) {
		is EmptyExpression -> empty.valueEvaluation()
		is LinkExpression -> link.valueEvaluation
		is GetExpression -> get.valueEvaluation
		is InvokeExpression -> invoke.lineEvaluation
		is SwitchExpression -> switch.valueEvaluation
		is VariableExpression -> variable.valueEvaluation()

	}

fun <T> Empty.valueEvaluation(): Evaluation<T, Value<T>> =
	value<T>().evaluation()

val <T> Link<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	expression.valueEvaluation.bind { value ->
		line.lineEvaluation.map { line ->
			value.plus(line)
		}
	}

val <T> Line<T>.lineEvaluation: Evaluation<T, ValueLine<T>> get() =
	when (this) {
		is AnyLine -> any.anyLineEvaluation
		is FieldLine -> field.lineEvaluation
		is LiteralLine -> literal.lineEvaluation()
		is FunctionLine -> function.lineEvaluation
	}

val <T> T.anyLineEvaluation: Evaluation<T, ValueLine<T>> get() =
	anyValueLine(this).evaluation()

val <T> Field<T>.lineEvaluation: Evaluation<T, ValueLine<T>> get() =
	expression.valueEvaluation.map { name lineTo it }

fun <T> Literal.lineEvaluation(): Evaluation<T, ValueLine<T>> =
	valueLine<T>(this).evaluation()

val <T> leo.named.expression.Function<T>.lineEvaluation: Evaluation<T, ValueLine<T>> get() =
	line(function(body)).evaluation()

val <T> Get<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	expression.valueEvaluation.map { it.get(name) }

val <T> Invoke<T>.lineEvaluation: Evaluation<T, Value<T>> get() =
	function.valueEvaluation.bind { functionValue ->
		functionValue.unsafeFunction.let { function ->
			params.valueEvaluation.bind { paramsValue ->
				dictionaryEvaluation<T>().map { dictionary ->
					dictionary
						.plus(paramsValue.dictionary)
						.value(function.body)
				}
			}
		}
	}

val <T> Switch<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	expression.valueEvaluation.bind { value ->
		value.unsafeLine.let { valueLine ->
			dictionaryEvaluation<T>().map { dictionary ->
				dictionary.plus(valueLine.definition).value(expression(valueLine.name))
			}
		}
	}

fun <T> Variable.valueEvaluation(): Evaluation<T, Value<T>> =
	dictionaryEvaluation<T>().map { it.value(type) }
