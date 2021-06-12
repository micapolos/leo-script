package leo.named.evaluator

import leo.Literal
import leo.Stateful
import leo.bind
import leo.flat
import leo.getStateful
import leo.map
import leo.named.expression.AnyLine
import leo.named.expression.Field
import leo.named.expression.FieldLine
import leo.named.expression.FunctionLine
import leo.named.expression.Get
import leo.named.expression.GetLine
import leo.named.expression.Invoke
import leo.named.expression.InvokeLine
import leo.named.expression.Line
import leo.named.expression.LiteralLine
import leo.named.expression.Switch
import leo.named.expression.SwitchLine
import leo.named.expression.Variable
import leo.named.expression.VariableLine
import leo.named.expression.expression
import leo.named.expression.line
import leo.named.expression.unsafeLine
import leo.named.value.Value
import leo.named.value.ValueLine
import leo.named.value.anyValueLine
import leo.named.value.function
import leo.named.value.get
import leo.named.value.line
import leo.named.value.lineTo
import leo.named.value.name
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
	lineStack.map { lineEvaluation }.flat.map(::Value)

fun <T> Dictionary<T>.lineEvaluation(line: Line<T>): Evaluation<T, ValueLine<T>> =
	TODO()

val <T> Line<T>.lineEvaluation: Evaluation<T, ValueLine<T>> get() =
	when (this) {
		is AnyLine -> any.anyLineEvaluation
		is FieldLine -> field.lineEvaluation
		is FunctionLine -> function.lineEvaluation
		is GetLine -> get.lineEvaluation
		is InvokeLine -> invoke.lineEvaluation
		is LiteralLine -> literal.lineEvaluation()
		is SwitchLine -> switch.lineEvaluation
		is VariableLine -> variable.lineEvaluation()
	}

val <T> T.anyLineEvaluation: Evaluation<T, ValueLine<T>> get() =
	anyValueLine(this).evaluation()

val <T> Field<T>.lineEvaluation: Evaluation<T, ValueLine<T>> get() =
	expression.valueEvaluation.map { name lineTo it }

val <T> leo.named.expression.Function<T>.lineEvaluation: Evaluation<T, ValueLine<T>> get() =
	line(function(expression(bodyLine))).evaluation()

val <T> Get<T>.lineEvaluation: Evaluation<T, ValueLine<T>> get() =
	line.lineEvaluation.map { it.get(name) }

val <T> Invoke<T>.lineEvaluation: Evaluation<T, ValueLine<T>> get() =
	function.lineEvaluation.bind { functionValue ->
		(functionValue as leo.named.value.FunctionValueLine).function.let { function ->
			params.valueEvaluation.bind { paramsStructure ->
				dictionaryEvaluation<T>().map { dictionary ->
					dictionary
						.plus(paramsStructure.dictionary)
						.valueLine(function.expression.unsafeLine)
				}
			}
		}
	}

fun <T> Literal.lineEvaluation(): Evaluation<T, ValueLine<T>> =
	valueLine<T>(this).evaluation()

val <T> Switch<T>.lineEvaluation: Evaluation<T, ValueLine<T>> get() =
	lhs.lineEvaluation.bind { value ->
		dictionaryEvaluation<T>().map { dictionary ->
			dictionary.plus(value.definition).valueLine(line(value.name))
		}
	}

fun <T> Variable.lineEvaluation(): Evaluation<T, ValueLine<T>> =
	dictionaryEvaluation<T>().map { it.value(type) }
