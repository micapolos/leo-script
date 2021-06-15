package leo.named.evaluator

import leo.Empty
import leo.Literal
import leo.Stateful
import leo.bind
import leo.fold
import leo.getStateful
import leo.map
import leo.named.expression.AnyLine
import leo.named.expression.Bind
import leo.named.expression.BindExpression
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
import leo.named.expression.With
import leo.named.expression.WithExpression
import leo.named.expression.expression
import leo.named.value.Value
import leo.named.value.ValueLine
import leo.named.value.anyValueLine
import leo.named.value.function
import leo.named.value.get
import leo.named.value.invoke
import leo.named.value.line
import leo.named.value.lineTo
import leo.named.value.name
import leo.named.value.plus
import leo.named.value.unsafeFunction
import leo.named.value.unsafeSwitchLine
import leo.named.value.value
import leo.reverse
import leo.stateful

typealias Evaluation<V> = Stateful<Dictionary, V>
fun <V> V.evaluation(): Evaluation<V> = stateful()

val dictionaryEvaluation: Evaluation<Dictionary> get() = getStateful()

val leo.named.expression.Expression.valueEvaluation: Evaluation<Value> get() =
	when (this) {
		is EmptyExpression -> empty.valueEvaluation()
		is LinkExpression -> link.valueEvaluation
		is GetExpression -> get.valueEvaluation
		is InvokeExpression -> invoke.valueEvaluation
		is WithExpression -> with.valueEvaluation
		is BindExpression -> bind.valueEvaluation
		is SwitchExpression -> switch.valueEvaluation
		is VariableExpression -> variable.valueEvaluation()
	}

fun Empty.valueEvaluation(): Evaluation<Value> =
	value().evaluation()

val Link.valueEvaluation: Evaluation<Value> get() =
	expression.valueEvaluation.bind { value ->
		line.lineEvaluation.map { line ->
			value.plus(line)
		}
	}

val With.valueEvaluation: Evaluation<Value> get() =
	lhs.valueEvaluation.bind { lhsValue ->
		rhs.valueEvaluation.map { rhsValue ->
			lhsValue.fold(rhsValue.lineStack.reverse) { plus(it) }
		}
	}

val Line.lineEvaluation: Evaluation<ValueLine> get() =
	when (this) {
		is AnyLine -> any.anyLineEvaluation
		is FieldLine -> field.lineEvaluation
		is LiteralLine -> literal.lineEvaluation()
		is FunctionLine -> function.lineEvaluation
	}

val Any?.anyLineEvaluation: Evaluation<ValueLine> get() =
	anyValueLine(this).evaluation()

val Field.lineEvaluation: Evaluation<ValueLine> get() =
	expression.valueEvaluation.map { name lineTo it }

fun Literal.lineEvaluation(): Evaluation<ValueLine> =
	any.anyValueLine.evaluation()

val leo.named.expression.Function.lineEvaluation: Evaluation<ValueLine> get() =
	dictionaryEvaluation.map { dictionary ->
		line(function(dictionary, body))
	}

val Get.valueEvaluation: Evaluation<Value> get() =
	expression.valueEvaluation.map { it.get(name) }

val Invoke.valueEvaluation: Evaluation<Value> get() =
	function.valueEvaluation.bind { functionValue ->
		functionValue.unsafeFunction.let { function ->
			params.valueEvaluation.map { paramsValue ->
				function.invoke(paramsValue)
			}
		}
	}

val Bind.valueEvaluation: Evaluation<Value> get() =
	dictionaryEvaluation.bind { dictionary ->
		binding.expression.valueEvaluation.map { value ->
			dictionary
				.plus(definition(binding.type, binding(value)))
				.value(expression)
		}
	}

val Switch.valueEvaluation: Evaluation<Value> get() =
	expression.valueEvaluation.bind { value ->
		value.unsafeSwitchLine.let { valueLine ->
			dictionaryEvaluation.map { dictionary ->
				dictionary.plus(valueLine.definition).value(this.expression(valueLine.name))
			}
		}
	}

fun Variable.valueEvaluation(): Evaluation<Value> =
	dictionaryEvaluation.map { it.value(type) }
