package leo.named.evaluator

import leo.Script
import leo.get
import leo.named.compiler.dictionary
import leo.named.compiler.plusCompilation
import leo.named.compiler.typedExpression
import leo.named.compiler.unitEnvironment
import leo.named.library.preludeCompiler
import leo.named.value.Value

val Script.value: Value get() =
	typedExpression(dictionary()).expression.value

val Script.preludeValue: Value get() =
	preludeCompiler.plusCompilation(this).get(unitEnvironment).typedExpression.expression.value
