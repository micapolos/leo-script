package leo.typed.indexed.python

import leo.any
import leo.typed.compiler.python.Python
import leo.typed.indexed.BooleanExpression
import leo.typed.indexed.ConditionalExpression
import leo.typed.indexed.EmptyExpression
import leo.typed.indexed.Expression
import leo.typed.indexed.FunctionExpression
import leo.typed.indexed.GetExpression
import leo.typed.indexed.IndexExpression
import leo.typed.indexed.InvokeExpression
import leo.typed.indexed.NativeExpression
import leo.typed.indexed.RecursiveExpression
import leo.typed.indexed.SwitchExpression
import leo.typed.indexed.TupleExpression
import leo.typed.indexed.VariableExpression

val Expression<Python>.containsMath: Boolean get() =
  when (this) {
    is BooleanExpression ->
      false
    is ConditionalExpression ->
      conditional.condition.containsMath
          || conditional.trueCase.containsMath
          || conditional.falseCase.containsMath
    is EmptyExpression ->
      false
    is FunctionExpression ->
      function.expression.containsMath
    is GetExpression ->
      get.lhs.containsMath
    is IndexExpression ->
      false
    is InvokeExpression ->
      invoke.lhs.containsMath || invoke.paramStack.any { containsMath }
    is NativeExpression ->
      native.string.contains("math.")
    is RecursiveExpression ->
      recursive.function.expression.containsMath
    is SwitchExpression ->
      switch.lhs.containsMath || switch.caseStack.any { containsMath }
    is TupleExpression ->
      tuple.expressionStack.any { containsMath }
    is VariableExpression ->
      false
  }
