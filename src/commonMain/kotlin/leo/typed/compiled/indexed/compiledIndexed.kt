package leo.typed.compiled.indexed

import leo.Empty
import leo.IndexVariable
import leo.array
import leo.base.map
import leo.base.mapIndexed
import leo.base.stack
import leo.empty
import leo.getFromBottom
import leo.isSimple
import leo.lineCount
import leo.map
import leo.onlyOrNull
import leo.pushAll
import leo.seq
import leo.size
import leo.stack
import leo.typed.compiled.ApplyExpression
import leo.typed.compiled.Compiled
import leo.typed.compiled.ContentExpression
import leo.typed.compiled.EmptyExpression
import leo.typed.compiled.LinkExpression
import leo.typed.compiled.SelectExpression
import leo.typed.compiled.SwitchExpression
import leo.typed.compiled.VariableExpression
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
  expression.indexedExpression

val <V> leo.typed.compiled.Expression<V>.indexedExpression: Expression<V> get() =
  when (this) {
    is ApplyExpression -> apply.indexedExpression
    is SelectExpression -> select.indexedExpression
    is SwitchExpression -> switch.indexedExpression
    is ContentExpression -> content.indexedExpression
    is VariableExpression -> variable.indexedExpression()
    is LinkExpression -> link.indexedExpression
    is EmptyExpression -> empty.indexedExpression()
  }

fun <V> Empty.indexedExpression(): Expression<V> =
  expression(this)

val <V> leo.typed.compiled.Line<V>.indexedExpression: Expression<V> get() =
  when (this) {
    is leo.typed.compiled.FieldLine -> field.indexedExpression
    is leo.typed.compiled.FunctionLine -> function.indexedExpression
    is leo.typed.compiled.GetLine -> get.indexedExpression
    is leo.typed.compiled.NativeLine -> nativeExpression(native)
  }

val <V> leo.typed.compiled.Apply<V>.indexedExpression: Expression<V> get() =
  lhs.indexedFragment.let { fragment ->
    when (fragment.tail.arity) {
      0 ->
        expression(
          invoke(
            rhs.indexedExpression,
            *fragment.tuple.expressionStack.array))
      1 ->
        expression(
          invoke(
            rhs.indexedExpression,
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
                    rhs.indexedExpression,
                    *stack(*0.until(fragment.tail.arity).map { expression<V>(variable(0)).get(it) }.toTypedArray())
                      .pushAll(fragment.tuple.expressionStack)
                      .array)))),
            fragment.tail.expression))
    }
  }

val <V> leo.typed.compiled.Select<V>.indexedExpression: Expression<V> get() =
  if (choice.isSimple) indexExpression
  else expression(indexExpression, case.line.indexedExpression)

val <V> leo.typed.compiled.Select<V>.indexExpression: Expression<V> get() =
  choice.indexedLineOrNull(case.name)!!.let {
    if (choice.lineStack.size == 2) expression(it.index == 0)
    else expression(it.index)
  }

val <V> leo.typed.compiled.Switch<V>.indexedExpression: Expression<V> get() =
  lhs.compiledChoice.let { compiledChoice ->
    if (compiledChoice.choice.isSimple)
      if (compiledChoice.choice.lineStack.size == 2)
        compiledChoice.expression.indexedExpression.ifThenElse(
          caseStack.getFromBottom(0)!!.indexedExpression,
          caseStack.getFromBottom(1)!!.indexedExpression)
      else
        compiledChoice.expression.indexedExpression
          .switch(*caseStack.map { indexedExpression }.array)
    else
      compiledChoice.expression.indexedExpression.indirect {
        if (compiledChoice.choice.lineStack.size == 2)
          it.get(0)
            .ifThenElse(
              expression(
                function(
                  1,
                  caseStack
                    .getFromBottom(0)!!
                    .indexedExpression)),
              expression(
                function(
                  1,
                  caseStack
                    .getFromBottom(1)!!
                    .indexedExpression)))
            .invoke(it.get(1))
        else
          it.get(0)
            .switch(
              *caseStack
                .seq
                .mapIndexed
                .map {
                  expression(function(1, value.indexedExpression))
                }
                .stack
                .array)
            .invoke(it.get(1))
      }
  }

val <V> Compiled<V>.indexedFragment: ExpressionFragment<V> get() =
  expression.indexedFragment(type.lineCount)

fun <V> leo.typed.compiled.Expression<V>.indexedFragment(arity: Int): ExpressionFragment<V> =
  when (this) {
    is leo.typed.compiled.LinkExpression ->
      link.indexedFragment
    else ->
      ExpressionFragment(ExpressionTail(indexedExpression, arity), ExpressionTuple(stack()))
  }

val <V> leo.typed.compiled.Link<V>.indexedExpression: Expression<V> get() =
  indexedFragment.let { fragment ->
    when (fragment.tail.arity) {
      0 ->
        when (fragment.tuple.expressionStack.size) {
          0 -> expression(empty)
          1 -> fragment.tuple.expressionStack.onlyOrNull!!
          else -> expression(fragment.tuple)
        }
      1 ->
        when (fragment.tuple.expressionStack.size) {
          0 -> fragment.tail.expression
          else -> expression(tuple(fragment.tail.expression, *fragment.tuple.expressionStack.array))
        }
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

val <V> leo.typed.compiled.Link<V>.indexedFragment: ExpressionFragment<V> get() =
  lhsCompiled.expression.indexedFragment(lhsCompiled.type.lineCount)
    .plus(rhsCompiledLine.line.indexedExpression)

val <V> leo.typed.compiled.Field<V>.indexedExpression: Expression<V> get() =
  rhs.indexedExpression

val <V> leo.typed.compiled.Get<V>.indexedExpression: Expression<V> get() =
  when (lhs.type.lineCount) {
    0 -> lhs.indexedExpression
    1 -> lhs.indexedExpression
    else -> lhs.indexedExpression.get(index)
  }

val <V> leo.typed.compiled.Content<V>.indexedExpression: Expression<V> get() =
  lhs.indexedExpression

val <V> leo.typed.compiled.Function<V>.indexedExpression: Expression<V> get() =
  function(
    paramType.lineCount,
    body.compiled.indexedExpression)
    .let { function ->
    if (body.isRecursive) expression(recursive(function))
    else expression(function)
  }

//fun <V> leo.typed.compiled.Bind<V>.indexedExpression(scope: Scope): Expression<V> =
//  expression(function(1, compiled.indexedExpression(scope.plus(binding.type))))
//    .invoke(binding.compiled.indexedExpression(scope))

fun <V> IndexVariable.indexedExpression(): Expression<V> =
  expression(variable(index))
