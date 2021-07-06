package leo.term.compiler.runtime

import leo.Script
import leo.term.Value
import leo.term.compiler.typedTerm
import leo.term.typed.TypedValue
import leo.term.typed.typed
import leo.term.value

val Script.typedValue: TypedValue<Thing> get() =
	thingEnvironment.typedTerm(this).let { typedTerm ->
		typed(thingEvaluator.value(typedTerm.v), typedTerm.t)
	}

val Script.value: Value<Thing> get() = typedValue.v
