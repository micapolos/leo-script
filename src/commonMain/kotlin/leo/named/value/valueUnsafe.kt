package leo.named.value

import leo.base.notNullOrError
import leo.onlyOrNull

val <T> Value<T>.unsafeLine: ValueLine<T> get() =
	lineStack.onlyOrNull.notNullOrError("$this not a single line")

val <T> Value<T>.unsafeFunction: ValueFunction<T> get() =
	(unsafeLine as? FunctionValueLine<T>)?.function.notNullOrError("$this not a function")