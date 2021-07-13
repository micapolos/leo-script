package leo.term.compiled.indexed

import leo.ChoiceType
import leo.IndexVariable
import leo.StructureType
import leo.choiceOrNull
import leo.isSimple
import leo.lineCount
import leo.map
import leo.size
import leo.term.compiled.Compiled
import leo.term.indexed.Expression
import leo.term.indexed.ExpressionFunction
import leo.term.indexed.ExpressionGet
import leo.term.indexed.ExpressionIndexedSwitch
import leo.term.indexed.ExpressionInvoke
import leo.term.indexed.ExpressionRecursive
import leo.term.indexed.ExpressionSwitch
import leo.term.indexed.ExpressionTuple
import leo.term.indexed.FunctionExpression
import leo.term.indexed.GetExpression
import leo.term.indexed.IndexSwitchExpression
import leo.term.indexed.IndexedSwitchExpression
import leo.term.indexed.InvokeExpression
import leo.term.indexed.NativeExpression
import leo.term.indexed.RecursiveExpression
import leo.term.indexed.TupleExpression
import leo.term.indexed.VariableExpression
import leo.term.indexed.expression
import leo.term.indexed.index
import leo.term.indexed.indexed
import leo.toList

val <V> Compiled<V>.indexedExpression: Expression<V> get() =
  when (expression) {
    is leo.term.compiled.ApplyExpression -> expression.apply.indexedExpression
    is leo.term.compiled.SelectExpression -> expression.select.indexedExpression
    is leo.term.compiled.SwitchExpression -> expression.switch.indexedExpression
    is leo.term.compiled.TupleExpression -> expression.tuple.indexedExpression
    is leo.term.compiled.VariableExpression -> expression.variable.indexedExpression()
  }

val <V> leo.term.compiled.Line<V>.indexedExpression: Expression<V> get() =
  when (this) {
    is leo.term.compiled.FieldLine -> field.indexedExpression
    is leo.term.compiled.FunctionLine -> function.indexedExpression
    is leo.term.compiled.GetLine -> get.indexedExpression
    is leo.term.compiled.NativeLine -> NativeExpression(native)
  }

val <V> leo.term.compiled.Apply<V>.indexedExpression: Expression<V> get() =
  InvokeExpression(
    when (lhs.type) {
      is ChoiceType -> null
      is StructureType ->
        when (lhs.expression) {
          is leo.term.compiled.TupleExpression ->
              ExpressionInvoke(
                rhs.indexedExpression,
                lhs.expression.tuple.lineStack.map { indexedExpression }.toList())
          else -> null
        }
    } ?: when (lhs.type.lineCount) {
      0 -> ExpressionInvoke(rhs.indexedExpression, listOf())
      1 -> ExpressionInvoke(rhs.indexedExpression, listOf(lhs.indexedExpression))
      else -> ExpressionInvoke(
        FunctionExpression(ExpressionFunction(1, rhs.indexedExpression)),
        listOf(
          InvokeExpression(
            ExpressionInvoke(
              lhs.indexedExpression,
              0.until(lhs.type.lineCount).map { VariableExpression<V>(IndexVariable(it)) }.toList()))))
    }
  )

val <V> leo.term.compiled.Select<V>.indexedExpression: Expression<V> get() =
  line.indexedExpression.let { lineScheme ->
    if (choice.isSimple) expression(index(index, choice.lineStack.size))
    else expression(indexed(index, choice.lineStack.size, lineScheme))
  }

val <V> leo.term.compiled.Switch<V>.indexedExpression: Expression<V> get() =
  lhs.type.choiceOrNull!!.let { choice ->
    if (choice.isSimple)
      IndexSwitchExpression(
        ExpressionSwitch(
          lhs.indexedExpression,
          lineStack.map { indexedExpression }.toList()))
    else
      IndexedSwitchExpression(
        ExpressionIndexedSwitch(
          lhs.indexedExpression,
          lineStack.map { indexedExpression }.toList()))
  }

val <V> leo.term.compiled.Tuple<V>.indexedExpression: Expression<V> get() =
  TupleExpression(ExpressionTuple(lineStack.map { indexedExpression }.toList()))

val <V> leo.term.compiled.Field<V>.indexedExpression: Expression<V> get() =
  rhs.indexedExpression

val <V> leo.term.compiled.Get<V>.indexedExpression: Expression<V> get() =
  when (lhs.type.lineCount) {
    1 -> lhs.indexedExpression
    else -> GetExpression(ExpressionGet(lhs.indexedExpression, index))
  }

val <V> leo.term.compiled.Function<V>.indexedExpression: Expression<V> get() =
  ExpressionFunction(paramType.lineCount, body.compiled.indexedExpression).let { function ->
    if (body.isRecursive) RecursiveExpression(ExpressionRecursive(function))
    else FunctionExpression(function)
  }

fun <V> leo.term.IndexVariable.indexedExpression(): Expression<V> =
  VariableExpression(IndexVariable(index))