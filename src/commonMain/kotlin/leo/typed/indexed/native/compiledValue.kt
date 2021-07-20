package leo.typed.indexed.native

import leo.typed.compiled.Compiled
import leo.typed.compiled.indexed.indexedExpression
import leo.typed.compiler.native.Native
import leo.typed.indexed.Value
import leo.typed.indexed.value

val Compiled<Native>.value: Value<Native> get() =
  indexedExpression.value(nativeEvaluator)