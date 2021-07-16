package leo.term.indexed

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
      invoke.lhs.containsRecursion || invoke.params.any { it.containsRecursion }
    is NativeExpression -> false
    is RecursiveExpression -> true
    is SwitchExpression ->
      switch.lhs.containsRecursion || switch.cases.any { it.containsRecursion }
    is TupleExpression ->
      tuple.expressionList.any { it.containsRecursion }
    is VariableExpression ->
      false
  }
