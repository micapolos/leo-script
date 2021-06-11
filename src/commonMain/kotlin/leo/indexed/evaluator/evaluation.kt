package leo.indexed.evaluator

import leo.Literal
import leo.NumberLiteral
import leo.Stateful
import leo.StringLiteral
import leo.bind
import leo.flat
import leo.getStateful
import leo.indexed.AnyExpression
import leo.indexed.At
import leo.indexed.AtExpression
import leo.indexed.Expression
import leo.indexed.Function
import leo.indexed.FunctionExpression
import leo.indexed.IndexExpression
import leo.indexed.Invoke
import leo.indexed.InvokeExpression
import leo.indexed.LiteralExpression
import leo.indexed.Tuple
import leo.indexed.TupleExpression
import leo.indexed.Variable
import leo.indexed.VariableExpression
import leo.map
import leo.setStateful
import leo.stateful
import leo.toList

typealias Evaluation<T> = Stateful<Context, T>

val <T> T.evaluation: Evaluation<T> get() = stateful()

val contextEvaluation: Evaluation<Context> get() = getStateful()
val Context.setEvaluation: Evaluation<Unit> get() = setStateful(this)

val Expression<Value>.valueEvaluation: Evaluation<Value> get() =
	when (this) {
		is AnyExpression -> any.evaluation
		is AtExpression -> at.valueEvaluation
		is FunctionExpression -> function.valueEvaluation
		is IndexExpression -> index.evaluation
		is InvokeExpression -> invoke.valueEvaluation
		is LiteralExpression -> literal.value.evaluation
		is TupleExpression -> tuple.valueEvaluation
		is VariableExpression -> variable.valueEvaluation
	}

val At<Value>.valueEvaluation: Evaluation<Value> get() =
	vector.valueEvaluation.bind { vector ->
		index.valueEvaluation.map { index ->
			vector.valueList[index.valueInt]
		}
	}

val Function<Value>.valueEvaluation: Evaluation<Value> get() =
	contextEvaluation.map { context ->
		value { list ->
			context.push(list).evaluate(body)
		}
	}

val Invoke<Value>.valueEvaluation: Evaluation<Value> get() =
	function.valueEvaluation.bind { functionValue ->
		params.valueEvaluation.map { paramsValue ->
			functionValue.valueFn.invoke(paramsValue.valueList)
		}
	}

val Literal.value: Value get() =
	when (this) {
		is NumberLiteral -> number.double
		is StringLiteral -> string
	}

val Tuple<Value>.valueEvaluation: Evaluation<Value> get() =
	expressionStack.map { valueEvaluation }.flat.map { it.toList() }

val Variable.valueEvaluation: Evaluation<Value> get() =
	contextEvaluation.map { context ->
		context.value(this)
	}