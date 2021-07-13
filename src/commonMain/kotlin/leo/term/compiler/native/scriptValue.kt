package leo.term.compiler.native

import leo.Script
import leo.term.Value
import leo.term.compiler.compiled
import leo.term.typed.TypedValue

val Script.typedValue: TypedValue<Native>
  get() =
    nativeEnvironment.compiled(this).let { typedTerm ->
      TODO()
      //typed(nativeEvaluator.value(typedTerm.v), typedTerm.t)
    }

val Script.value: Value<Native> get() = typedValue.v
