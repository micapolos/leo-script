package leo.typed.compiled.indexed

import leo.EmptyStack
import leo.LinkStack
import leo.array
import leo.base.map
import leo.base.mapIndexed
import leo.base.runIf
import leo.base.stack
import leo.empty
import leo.get
import leo.getFromBottom
import leo.isSimple
import leo.lineCount
import leo.map
import leo.name
import leo.pushAll
import leo.seq
import leo.size
import leo.stack
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiled.compiledChoice
import leo.typed.compiled.indexedLineOrNull
import leo.typed.indexed.Expression
import leo.typed.indexed.ExpressionFragment
import leo.typed.indexed.ExpressionTail
import leo.typed.indexed.ExpressionTuple
import leo.typed.indexed.expression
import leo.typed.indexed.function
import leo.typed.indexed.get
import leo.typed.indexed.ifThenElse
import leo.typed.indexed.indirect
import leo.typed.indexed.invoke
import leo.typed.indexed.nativeExpression
import leo.typed.indexed.plus
import leo.typed.indexed.recursive
import leo.typed.indexed.switch
import leo.typed.indexed.tuple
import leo.variable

val <V> Compiled<V>.indexedExpression: Expression<V> get() =
  indexedExpression(scope())

fun <V> Compiled<V>.indexedExpression(scope: Scope): Expression<V> =
  expression.indexedExpression(scope)

fun <V> leo.typed.compiled.Expression<V>.indexedExpression(scope: Scope): Expression<V> =
  when (this) {
    is leo.typed.compiled.ApplyExpression -> apply.indexedExpression(scope)
    is leo.typed.compiled.SelectExpression -> select.indexedExpression(scope)
    is leo.typed.compiled.SwitchExpression -> switch.indexedExpression(scope)
    is leo.typed.compiled.TupleExpression -> tuple.indexedExpression(scope)
    is leo.typed.compiled.ContentExpression -> content.indexedExpression(scope)
    is leo.typed.compiled.BindExpression -> bind.indexedExpression(scope)
    is leo.typed.compiled.VariableExpression -> variable.indexedExpression(scope)
    is leo.typed.compiled.LinkExpression -> link.indexedExpression(scope)
  }

fun <V> leo.typed.compiled.Line<V>.indexedExpression(scope: Scope): Expression<V> =
  when (this) {
    is leo.typed.compiled.FieldLine -> field.indexedExpression(scope)
    is leo.typed.compiled.FunctionLine -> function.indexedExpression(scope)
    is leo.typed.compiled.GetLine -> get.indexedExpression(scope)
    is leo.typed.compiled.NativeLine -> nativeExpression(native)
  }

fun <V> leo.typed.compiled.Apply<V>.indexedExpression(scope: Scope): Expression<V> =
  lhs.indexedFragment(scope).let { fragment ->
    when (fragment.tail.arity) {
      0 ->
        expression(
          invoke(
            rhs.indexedExpression(scope),
            *fragment.tuple.expressionStack.array))
      1 ->
        expression(
          invoke(
            rhs.indexedExpression(scope),
            fragment.tail.expression,
            *fragment.tuple.expressionStack.array))
      else ->
        expression(
          invoke(
            expression(
              function(
                1,
                expression(
                  invoke(
                    rhs.indexedExpression(scope),
                    *stack(*0.until(fragment.tail.arity).map { expression<V>(variable(0)).get(it) }.toTypedArray())
                      .pushAll(fragment.tuple.expressionStack)
                      .array)))),
              fragment.tail.expression))
    }
  }

fun <V> leo.typed.compiled.Select<V>.indexedExpression(scope: Scope): Expression<V> =
  if (choice.isSimple) indexExpression(scope)
  else expression(indexExpression(scope), case.line.indexedExpression(scope))

fun <V> leo.typed.compiled.Select<V>.indexExpression(@Suppress("UNUSED_PARAMETER") scope: Scope): Expression<V> =
  choice.indexedLineOrNull(case.name)!!.let {
    if (choice.lineStack.size == 2) expression(it.index == 0)
    else expression(it.index)
  }

fun <V> leo.typed.compiled.Switch<V>.indexedExpression(scope: Scope): Expression<V> =
  lhs.compiledChoice.let { compiledChoice ->
    if (compiledChoice.choice.isSimple)
      if (compiledChoice.choice.lineStack.size == 2)
        compiledChoice.expression.indexedExpression(scope).ifThenElse(
          caseStack.getFromBottom(0)!!.indexedExpression(scope),
          caseStack.getFromBottom(1)!!.indexedExpression(scope))
      else
        compiledChoice.expression.indexedExpression(scope)
          .switch(*caseStack.map { indexedExpression(scope) }.array)
    else
      compiledChoice.expression.indexedExpression(scope).indirect {
        if (compiledChoice.choice.lineStack.size == 2)
          it.get(0)
            .ifThenElse(
              expression(
                function(
                  1,
                  caseStack
                    .getFromBottom(0)!!
                    .indexedExpression(
                      scope.plus(type(compiledChoice.choice.lineStack.getFromBottom(0)!!.name))))),
              expression(
                function(
                  1,
                  caseStack
                    .getFromBottom(1)!!
                    .indexedExpression(
                      scope.plus(
                        type(compiledChoice.choice.lineStack.getFromBottom(1)!!.name))))))
            .invoke(it.get(1))
        else
          it.get(0)
            .switch(
              *caseStack
                .seq
                .mapIndexed
                .map {
                  expression(function(1, value.indexedExpression(scope.plus(type(compiledChoice.choice.lineStack.get(index)!!.name)))))
                }
                .stack
                .array)
            .invoke(it.get(1))
      }
  }

fun <V> leo.typed.compiled.Tuple<V>.indexedExpression(scope: Scope): Expression<V> =
  indexedTuple(scope).let { tuple ->
    when (tuple.expressionStack) {
      is EmptyStack -> expression(empty)
      is LinkStack ->
        when (tuple.expressionStack.link.tail) {
          is EmptyStack -> tuple.expressionStack.link.head
          is LinkStack -> expression(tuple)
        }
    }
  }

fun <V> leo.typed.compiled.Tuple<V>.indexedTuple(scope: Scope): ExpressionTuple<V> =
  ExpressionTuple(lineStack.map { indexedExpression(scope) })

fun <V> Compiled<V>.indexedFragment(scope: Scope): ExpressionFragment<V> =
  expression.indexedFragment(scope, type.lineCount)

fun <V> leo.typed.compiled.Expression<V>.indexedFragment(scope: Scope, arity: Int): ExpressionFragment<V> =
  when (this) {
    is leo.typed.compiled.TupleExpression ->
      ExpressionFragment(ExpressionTail(expression(empty), 0), tuple.indexedTuple(scope))
    is leo.typed.compiled.LinkExpression ->
      link.indexedFragment(scope)
    else ->
      ExpressionFragment(ExpressionTail(indexedExpression(scope), arity), ExpressionTuple(stack()))
  }

fun <V> leo.typed.compiled.Link<V>.indexedExpression(scope: Scope): Expression<V> =
  indexedFragment(scope).let { fragment ->
    when (fragment.tail.arity) {
      0 -> expression(fragment.tuple)
      1 -> expression(tuple(fragment.tail.expression, *fragment.tuple.expressionStack.array))
      else ->
        expression(
          invoke(
            expression(
              function(
                1,
                expression(
                  tuple(
                    *stack(*0.until(fragment.tail.arity).map { expression<V>(variable(0)).get(it) }.toTypedArray())
                      .pushAll(fragment.tuple.expressionStack)
                      .array)))),
            fragment.tail.expression))
    }
  }

fun <V> leo.typed.compiled.Link<V>.indexedFragment(scope: Scope): ExpressionFragment<V> =
  lhsCompiled.expression.indexedFragment(scope, lhsCompiled.type.lineCount)
    .plus(rhsCompiledLine.line.indexedExpression(scope))

fun <V> leo.typed.compiled.Field<V>.indexedExpression(scope: Scope): Expression<V> =
  rhs.indexedExpression(scope)

fun <V> leo.typed.compiled.Get<V>.indexedExpression(scope: Scope): Expression<V> =
  when (lhs.type.lineCount) {
    0 -> lhs.indexedExpression(scope)
    1 -> lhs.indexedExpression(scope)
    else -> lhs.indexedExpression(scope).get(index)
  }

fun <V> leo.typed.compiled.Content<V>.indexedExpression(scope: Scope): Expression<V> =
  lhs.indexedExpression(scope)

fun <V> leo.typed.compiled.Function<V>.indexedExpression(scope: Scope): Expression<V> =
  function(
    paramType.lineCount,
    body.compiled.indexedExpression(
      scope
        .runIf(body.isRecursive) { plus(paramType) }
        .plusNames(paramType)))
    .let { function ->
    if (body.isRecursive) expression(recursive(function))
    else expression(function)
  }

fun <V> leo.typed.compiled.Bind<V>.indexedExpression(scope: Scope): Expression<V> =
  expression(function(1, compiled.indexedExpression(scope.plus(binding.type))))
    .invoke(binding.compiled.indexedExpression(scope))

fun <V> leo.typed.compiled.TypeVariable.indexedExpression(scope: Scope): Expression<V> =
  expression(scope.indexVariable(this))
