package leo.indexed.evaluator

import leo.Stack
import leo.get
import leo.indexed.Expression
import leo.indexed.Variable
import leo.push
import leo.stack

data class Context(val stack: Stack<Value>)

val Stack<Value>.context get() = Context(this)

fun context(vararg values: Value) = Context(stack(*values))

fun Context.value(variable: Variable): Value =
	stack.get(variable.index)

fun Context.push(value: Value): Context =
	stack.push(value).context

fun Context.push(list: List<Value>): Context =
	list.fold(this, Context::push)

fun Context.evaluate(expression: Expression<Value>): Value =
	expression.valueEvaluation.get(this)