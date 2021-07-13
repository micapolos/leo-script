package leo.term.indexed

import leo.term.Scope
import leo.term.Value

data class Evaluator<V>(val valueFn: Scope<V>.(V) -> Value<V>)