package leo.named.value

import leo.base.notNullOrError
import leo.named.expression.Function
import leo.named.expression.FunctionLine
import leo.onlyOrNull

val <T> Value<T>.unsafeLine: ValueLine<T> get() =
	lineStack.onlyOrNull.notNullOrError("$this not a single line")

val <T> Value<T>.unsafeFunction: Function<T> get() =
	(unsafeLine as? FunctionLine<T>)?.function.notNullOrError("$this not a function")