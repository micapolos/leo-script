package leo.named.evaluator

import leo.Script
import leo.Type
import leo.get
import leo.named.compiler.context
import leo.named.compiler.plusCompilation
import leo.named.compiler.typedExpression
import leo.named.compiler.typedValue
import leo.named.compiler.unitEnvironment
import leo.named.library.preludeCompiler
import leo.named.typed.Typed
import leo.named.value.Value

val Script.value: Value
  get() =
    typedExpression(context()).expression.value

val Script.typedValue: Typed<Value, Type>
  get() =
    typedExpression(context()).typedValue

val Script.preludeTypedValue: Typed<Value, Type>
  get() =
    preludeCompiler.plusCompilation(this).get(unitEnvironment).typedExpression.typedValue

