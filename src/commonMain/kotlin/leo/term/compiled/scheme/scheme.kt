package leo.term.compiled.scheme

import leo.ChoiceType
import leo.StructureType
import leo.array
import leo.base.iterate
import leo.base.map
import leo.base.mapIndexed
import leo.base.reverse
import leo.base.reverseStack
import leo.isSimple
import leo.lineCount
import leo.map
import leo.seq
import leo.size
import leo.term.IndexVariable
import leo.term.compiled.Apply
import leo.term.compiled.ApplyExpression
import leo.term.compiled.Body
import leo.term.compiled.Compiled
import leo.term.compiled.Field
import leo.term.compiled.FieldLine
import leo.term.compiled.Function
import leo.term.compiled.FunctionLine
import leo.term.compiled.Get
import leo.term.compiled.GetLine
import leo.term.compiled.Line
import leo.term.compiled.NativeLine
import leo.term.compiled.Scope
import leo.term.compiled.Select
import leo.term.compiled.SelectExpression
import leo.term.compiled.Switch
import leo.term.compiled.SwitchExpression
import leo.term.compiled.Tuple
import leo.term.compiled.TupleExpression
import leo.term.compiled.VariableExpression
import leo.term.compiled.push
import leo.term.variable
import scheme.Scheme
import scheme.lhs
import scheme.plus
import scheme.rhs
import scheme.scheme
import scheme.tupleScheme
import scheme.vectorRef

fun Compiled<Scheme>.scheme(scope: Scope): Scheme =
  when (expression) {
    is ApplyExpression -> expression.apply.scheme(scope)
    is SelectExpression -> expression.select.scheme(scope)
    is SwitchExpression -> expression.switch.scheme(scope)
    is TupleExpression -> expression.tuple.scheme(scope)
    is VariableExpression -> expression.variable.scheme(scope)
  }

fun Apply<Scheme>.scheme(scope: Scope): Scheme =
  when (lhs.type) {
    is ChoiceType -> null
    is StructureType ->
      when (lhs.expression) {
        is TupleExpression -> scheme(rhs.scheme(scope), *lhs.expression.tuple.lineStack.map { scheme(scope) }.array)
        else -> null
      }
  } ?: TODO()

fun Tuple<Scheme>.scheme(scope: Scope): Scheme =
  tupleScheme(*lineStack.map { scheme(scope) }.array)

fun Line<Scheme>.scheme(scope: Scope): Scheme =
  when (this) {
    is NativeLine -> native
    is FieldLine -> field.rhs.scheme(scope)
    is FunctionLine -> function.scheme(scope)
    is GetLine -> get.scheme(scope)
  }

fun Field<Scheme>.scheme(scope: Scope): Scheme =
  rhs.scheme(scope)

fun Function<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("lambda"),
    scheme(*(0 until paramType.lineCount).map { scheme(variable(it + scope.depth)) }.toTypedArray()),
    body.scheme(scope.iterate(paramType.lineCount) { push }))

fun Body<Scheme>.scheme(scope: Scope): Scheme =
  if (!isRecursive) compiled.scheme(scope)
  else TODO()

fun Get<Scheme>.scheme(scope: Scope): Scheme =
  when (lhs.type.lineCount) {
    1 -> lhs.scheme(scope)
    2 -> lhs.scheme(scope).run { if (index == 0) lhs else rhs }
    else -> lhs.scheme(scope).vectorRef(scheme(index))
  }

fun IndexVariable.scheme(scope: Scope): Scheme =
  scheme(variable(scope.depth - index - 1))

fun Select<Scheme>.scheme(scope: Scope): Scheme =
  line.scheme(scope).let { lineScheme ->
    when (choice.lineStack.size) {
      1 -> lineScheme
      2 -> index.equals(0).scheme.let { indexScheme ->
        if (choice.isSimple) indexScheme
        else indexScheme.plus(lineScheme)
      }
      else ->
        scheme(index).let { indexScheme ->
          if (choice.isSimple) indexScheme
          else indexScheme.plus(lineScheme)
        }
    }
  }

fun Switch<Scheme>.scheme(scope: Scope): Scheme =
  scheme(
    scheme("case"),
    scheme(scheme("car"), lhs.scheme(scope)),
    scheme(*lineStack.seq.reverse.mapIndexed.map { scheme(scheme(index), scheme(scope.push)) }.reverseStack.array))

fun scheme(vararg schemes: Scheme) =
  ("(" + schemes.joinToString(", ") + ")").scheme

fun scheme(int: Int): Scheme = scheme("$int")

fun scheme(variable: IndexVariable): Scheme =
  scheme("v${variable.index}")
