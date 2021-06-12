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
import leo.named.value.FunctionValue
import leo.named.value.Structure
import leo.named.value.Value
import leo.named.value.anyValue
import leo.named.value.function
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

val <T> leo.named.expression.Expression<T>.structureEvaluation: Evaluation<T, Structure<T>> get() =
	lineStack.map { valueEvaluation }.flat.map(::Structure)

fun <T> Dictionary<T>.valueEvaluation(line: Line<T>): Evaluation<T, Value<T>> =
	TODO()

val <T> Line<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	when (this) {
		is AnyLine -> any.anyValueEvaluation
		is FieldLine -> field.valueEvaluation
		is FunctionLine -> function.valueEvaluation
		is GetLine -> get.valueEvaluation
		is InvokeLine -> invoke.valueEvaluation
		is LiteralLine -> literal.valueEvaluation()
		is SwitchLine -> switch.valueEvaluation
		is VariableLine -> variable.valueEvaluation()
	}

val <T> T.anyValueEvaluation: Evaluation<T, Value<T>> get() =
	anyValue(this).evaluation()

val <T> Field<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	expression.structureEvaluation.map { name valueTo it }

val <T> leo.named.expression.Function<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	value(function(expression(bodyLine))).evaluation()

val <T> Get<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	line.valueEvaluation.map { it.get(name) }

val <T> Invoke<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	function.valueEvaluation.bind { functionValue ->
		(functionValue as FunctionValue).function.let { function ->
			params.structureEvaluation.bind { paramsStructure ->
				dictionaryEvaluation<T>().map { dictionary ->
					dictionary
						.plus(paramsStructure.dictionary)
						.value(function.expression.unsafeLine)
				}
			}
		}
	}

fun <T> Literal.valueEvaluation(): Evaluation<T, Value<T>> =
	value<T>(this).evaluation()

val <T> Switch<T>.valueEvaluation: Evaluation<T, Value<T>> get() =
	lhs.valueEvaluation.bind { value ->
		dictionaryEvaluation<T>().map { dictionary ->
			dictionary.plus(value.definition).value(line(value.name))
		}
	}

fun <T> Variable.valueEvaluation(): Evaluation<T, Value<T>> =
	dictionaryEvaluation<T>().map { it.value(typeStructure) }
