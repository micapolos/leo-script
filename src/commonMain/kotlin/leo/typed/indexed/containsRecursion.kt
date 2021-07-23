package leo.typed.indexed

import leo.any

val Expression<*>.containsRecursion: Boolean get() =
  when (this) {
    is BooleanExpression ->
      false
    is ConditionalExpression ->
      conditional.condition.containsRecursion
          || conditional.trueCase.containsRecursion
          || conditional.falseCase.containsRecursion
    is EmptyExpression ->
      false
    is FunctionExpression ->
      function.expression.containsRecursion
    is GetExpression ->
      get.lhs.containsRecursion
    is IndexExpression ->
      false
    is InvokeExpression ->
      invoke.lhs.containsRecursion || invoke.paramStack.any { containsRecursion }
    is NativeExpression -> false
    is RecursiveExpression -> true
    is SwitchExpression ->
      switch.lhs.containsRecursion || switch.caseStack.any { containsRecursion }
    is TupleExpression ->
      tuple.expressionStack.any { containsRecursion }
    is VariableExpression ->
      false
  }
