package leo.named.compiler

import leo.Type
import leo.named.evaluator.value
import leo.named.typed.Typed
import leo.named.typed.TypedExpression
import leo.named.value.Value

val TypedExpression.typedValue: Typed<Value, Type> get() =
	Typed(expression.value, type)