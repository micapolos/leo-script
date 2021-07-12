package leo.term.compiled.indexed

import leo.ChoiceType
import leo.IndexVariable
import leo.StructureType
import leo.isSimple
import leo.lineCount
import leo.map
import leo.term.compiled.Compiled
import leo.term.indexed.Expression
import leo.term.indexed.Function
import leo.term.indexed.FunctionExpression
import leo.term.indexed.IndexExpression
import leo.term.indexed.Indexed
import leo.term.indexed.IndexedExpression
import leo.term.indexed.Invoke
import leo.term.indexed.InvokeExpression
import leo.term.indexed.NativeExpression
import leo.term.indexed.Recursive
import leo.term.indexed.RecursiveExpression
import leo.term.indexed.Tuple
import leo.term.indexed.TupleExpression
import leo.term.indexed.TupleGet
import leo.term.indexed.TupleGetExpression
import leo.term.indexed.VariableExpression
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
          is leo.term.compiled.TupleExpression -> Invoke(
            rhs.indexedExpression,
            lhs.expression.tuple.lineStack.map { indexedExpression }.toList())
          else -> null
        }
    } ?: when (lhs.type.lineCount) {
      0 -> Invoke(rhs.indexedExpression, listOf())
      1 -> Invoke(rhs.indexedExpression, listOf(lhs.indexedExpression))
      else -> TODO() // Must go through invoke, and get
    }
  )

val <V> leo.term.compiled.Select<V>.indexedExpression: Expression<V> get() =
  line.indexedExpression.let { lineScheme ->
    if (choice.isSimple) IndexExpression(index)
    else IndexedExpression(Indexed(index, lineScheme))
  }

val <V> leo.term.compiled.Switch<V>.indexedExpression: Expression<V> get() =
  TODO()

val <V> leo.term.compiled.Tuple<V>.indexedExpression: Expression<V> get() =
  TupleExpression(Tuple(lineStack.map { indexedExpression }.toList()))

val <V> leo.term.compiled.Field<V>.indexedExpression: Expression<V> get() =
  rhs.indexedExpression

val <V> leo.term.compiled.Get<V>.indexedExpression: Expression<V> get() =
  when (lhs.type.lineCount) {
    1 -> lhs.indexedExpression
    else -> TupleGetExpression(TupleGet(lhs.indexedExpression, index))
  }

val <V> leo.term.compiled.Function<V>.indexedExpression: Expression<V> get() =
  Function(paramType.lineCount, body.compiled.indexedExpression).let { function ->
    if (body.isRecursive) RecursiveExpression(Recursive(function))
    else FunctionExpression(function)
  }

fun <V> leo.term.IndexVariable.indexedExpression(): Expression<V> =
  VariableExpression(IndexVariable(index))