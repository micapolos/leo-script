package leo.term.compiler.runtime

import leo.Script
import leo.term.Value
import leo.term.compiler.typedTerm
import leo.term.typed.TypedValue
import leo.term.typed.typed
import leo.term.value

val Script.typedValue: TypedValue<Native> get() =
	nativeEnvironment.typedTerm(this).let { typedTerm ->
		typed(nativeEvaluator.value(typedTerm.v), typedTerm.t)
	}

val Script.value: Value<Native> get() = typedValue.v
