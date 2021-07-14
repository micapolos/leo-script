package leo.term.compiled.scheme

import leo.ChoiceType
import leo.StructureType
import leo.array
import leo.base.iterate
import leo.choiceOrNull
import leo.isSimple
import leo.lineCount
import leo.map
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
import scheme.nilScheme
import scheme.pair
import scheme.pairFirst
import scheme.pairSecond
import scheme.scheme
import scheme.switch
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
  } ?: when (lhs.type.lineCount) {
    0 -> scheme(rhs.scheme(scope))
    1 -> scheme(rhs.scheme(scope), lhs.scheme(scope))
    else -> scheme(
      scheme("apply"),
      rhs.scheme(scope),
      scheme(scheme("vector->list"), lhs.scheme(scope)))
  }

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
    else -> lhs.scheme(scope).vectorRef(scheme(index))
  }

fun IndexVariable.scheme(scope: Scope): Scheme =
  scheme(variable(scope.depth - index - 1))

fun Select<Scheme>.scheme(scope: Scope): Scheme =
  lineIndexed.line.scheme(scope).let { lineScheme ->
    scheme(lineIndexed.index).let { indexScheme ->
      if (choice.isSimple) indexScheme
      else pair(indexScheme, lineScheme)
    }
  }

fun Switch<Scheme>.scheme(scope: Scope): Scheme =
  lhs.type.choiceOrNull!!.let { choice ->
    if (choice.isSimple)
      scheme(
        scheme("let"),
        scheme(
          scheme(scheme("idx"), lhs.scheme(scope)),
          scheme(scheme(variable(scope.depth)), scheme("x").pairSecond)),
        scheme("idx").switch(*caseStack.map { scheme(scope.push) }.array))
    else
      scheme(
        scheme("let"),
        scheme(
          scheme(scheme("x"), lhs.scheme(scope)),
          scheme(scheme("idx"), scheme("x").pairFirst),
          scheme(scheme(variable(scope.depth)), nilScheme),
          scheme("idx").switch(*caseStack.map { scheme(scope.push) }.array)))
  }

fun scheme(vararg schemes: Scheme) =
  ("(" + schemes.joinToString(", ") + ")").scheme

fun scheme(int: Int): Scheme = scheme("$int")

fun scheme(variable: IndexVariable): Scheme =
  scheme("v${variable.index}")
