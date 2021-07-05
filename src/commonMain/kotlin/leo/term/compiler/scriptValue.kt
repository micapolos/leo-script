package leo.term.compiler

import leo.Script
import leo.term.Value
import leo.term.anyEvaluator
import leo.term.typed.TypedValue
import leo.term.typed.typed
import leo.term.value

val Script.typedValue: TypedValue<Any?> get() =
	context().compileTypedTerm(this).let { typedTerm ->
		typed(anyEvaluator.value(typedTerm.v), typedTerm.t)
	}

val Script.value: Value<Any?> get() =
	typedValue.value