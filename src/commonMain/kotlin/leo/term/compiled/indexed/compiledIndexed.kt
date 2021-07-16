package leo.term.compiled.indexed

import leo.ChoiceType
import leo.EmptyStack
import leo.LinkStack
import leo.StructureType
import leo.array
import leo.empty
import leo.getFromBottom
import leo.isSimple
import leo.lineCount
import leo.map
import leo.size
import leo.switchChoice
import leo.term.compiled.Compiled
import leo.term.compiled.rhs
import leo.term.indexed.Expression
import leo.term.indexed.expression
import leo.term.indexed.function
import leo.term.indexed.get
import leo.term.indexed.ifThenElse
import leo.term.indexed.indirect
import leo.term.indexed.invoke
import leo.term.indexed.nativeExpression
import leo.term.indexed.recursive
import leo.term.indexed.switch
import leo.term.indexed.tuple
import leo.variable

val <V> Compiled<V>.indexedExpression: Expression<V> get() =
  when (expression) {
    is leo.term.compiled.ApplyExpression -> expression.apply.indexedExpression
    is leo.term.compiled.SelectExpression -> expression.select.indexedExpression
    is leo.term.compiled.SwitchExpression -> expression.switch.indexedExpression
    is leo.term.compiled.TupleExpression -> expression.tuple.indexedExpression
    is leo.term.compiled.VariableExpression -> expression.variable.indexedExpression()
    is leo.term.compiled.ContentExpression -> expression.content.indexedExpression
  }

val <V> leo.term.compiled.Line<V>.indexedExpression: Expression<V> get() =
  when (this) {
    is leo.term.compiled.FieldLine -> field.indexedExpression
    is leo.term.compiled.FunctionLine -> function.indexedExpression
    is leo.term.compiled.GetLine -> get.indexedExpression
    is leo.term.compiled.NativeLine -> nativeExpression(native)
  }

val <V> leo.term.compiled.Apply<V>.indexedExpression: Expression<V> get() =
  expression(
    when (lhs.type) {
      is ChoiceType -> null
      is StructureType ->
        when (lhs.expression) {
          is leo.term.compiled.TupleExpression ->
              invoke(
                rhs.indexedExpression,
                *lhs.expression.tuple.lineStack.map { indexedExpression }.array)
          else -> null
        }
    } ?: when (lhs.type.lineCount) {
      0 -> invoke(rhs.indexedExpression)
      1 -> invoke(rhs.indexedExpression, lhs.indexedExpression)
      else -> invoke(
          expression(function(1, rhs.indexedExpression)),
          *0.until(lhs.type.lineCount).map { expression<V>(variable(it)) }.toTypedArray())
    }
  )

val <V> leo.term.compiled.Select<V>.indexedExpression: Expression<V> get() =
  if (choice.isSimple) indexExpression
  else expression(indexExpression, lineIndexed.line.indexedExpression)

val <V> leo.term.compiled.Select<V>.indexExpression: Expression<V> get() =
  if (choice.lineStack.size == 2) expression(lineIndexed.index == 0)
  else expression(lineIndexed.index)

val <V> leo.term.compiled.Switch<V>.indexedExpression: Expression<V> get() =
  lhs.type.switchChoice.let { choice ->
    if (choice.isSimple)
      if (choice.lineStack.size == 2)
        lhs.rhs.indexedExpression.ifThenElse(
          caseStack.getFromBottom(0)!!.indexedExpression,
          caseStack.getFromBottom(1)!!.indexedExpression)
      else
        lhs.rhs.indexedExpression.switch(*caseStack.map { indexedExpression }.array)
    else
      lhs.rhs.indexedExpression.indirect {
        if (choice.lineStack.size == 2)
          it.get(0).ifThenElse(
            expression(function(1, caseStack.getFromBottom(0)!!.indexedExpression)).invoke(it.get(1)),
            expression(function(1, caseStack.getFromBottom(1)!!.indexedExpression)).invoke(it.get(1)))
        else
          it.get(0)
            .switch(*caseStack.map { expression(function(1, indexedExpression)).invoke(it.get(1)) }.array)
      }
  }

val <V> leo.term.compiled.Tuple<V>.indexedExpression: Expression<V> get() =
  when (lineStack) {
    is EmptyStack -> expression(empty)
    is LinkStack ->
      when (lineStack.link.tail) {
        is EmptyStack -> lineStack.link.head.indexedExpression
        is LinkStack -> expression(tuple(*lineStack.map { indexedExpression }.array))
      }
  }

val <V> leo.term.compiled.Field<V>.indexedExpression: Expression<V> get() =
  rhs.indexedExpression

val <V> leo.term.compiled.Get<V>.indexedExpression: Expression<V> get() =
  when (lhs.type.lineCount) {
    0 -> lhs.indexedExpression
    1 -> lhs.indexedExpression
    else -> lhs.indexedExpression.get(index)
  }

val <V> leo.term.compiled.Content<V>.indexedExpression: Expression<V> get() =
  lhs.indexedExpression

val <V> leo.term.compiled.Function<V>.indexedExpression: Expression<V> get() =
  function(paramType.lineCount, body.compiled.indexedExpression).let { function ->
    if (body.isRecursive) expression(recursive(function))
    else expression(function)
  }

fun <V> leo.term.IndexVariable.indexedExpression(): Expression<V> =
  expression(variable(index))