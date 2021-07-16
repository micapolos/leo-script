package leo.term.indexed.python

import leo.Empty
import leo.IndexVariable
import leo.base.iterate
import leo.base.string
import leo.term.compiled.Scope
import leo.term.compiled.push
import leo.term.compiled.scope
import leo.term.compiler.python.Python
import leo.term.compiler.python.get
import leo.term.compiler.python.ifThenElse
import leo.term.compiler.python.invoke
import leo.term.compiler.python.python
import leo.term.compiler.python.tuplePython
import leo.term.indexed.BooleanExpression
import leo.term.indexed.ConditionalExpression
import leo.term.indexed.EmptyExpression
import leo.term.indexed.Expression
import leo.term.indexed.ExpressionConditional
import leo.term.indexed.ExpressionFunction
import leo.term.indexed.ExpressionGet
import leo.term.indexed.ExpressionInvoke
import leo.term.indexed.ExpressionRecursive
import leo.term.indexed.ExpressionSwitch
import leo.term.indexed.ExpressionTuple
import leo.term.indexed.FunctionExpression
import leo.term.indexed.GetExpression
import leo.term.indexed.IndexExpression
import leo.term.indexed.InvokeExpression
import leo.term.indexed.NativeExpression
import leo.term.indexed.RecursiveExpression
import leo.term.indexed.SwitchExpression
import leo.term.indexed.TupleExpression
import leo.term.indexed.VariableExpression
import leo.variable

val Expression<Python>.python: Python get() =
  python(
    string(
      "import operator;",
      "Z=lambda f:(lambda g:f(g(g)))(lambda g:f(lambda *y:g(g)(*y)));",
      python(scope()).string))

fun Expression<Python>.python(scope: Scope): Python =
  when (this) {
    is EmptyExpression -> python("`()")
    is InvokeExpression -> invoke.python(scope)
    is FunctionExpression -> function.python(scope)
    is RecursiveExpression -> recursive.python(scope)
    is GetExpression -> get.python(scope)
    is IndexExpression -> index.python(scope)
    is SwitchExpression -> switch.python(scope)
    is TupleExpression -> tuple.python(scope)
    is NativeExpression -> native
    is VariableExpression -> variable.python(scope)
    is BooleanExpression -> boolean.python(scope)
    is ConditionalExpression -> conditional.python(scope)
  }

@Suppress("unused")
val Empty.python: Python get() =
  python("()")

fun ExpressionInvoke<Python>.python(scope: Scope): Python =
  lhs.python(scope).invoke(*params.map { it.python(scope) }.toTypedArray())

fun ExpressionFunction<Python>.python(scope: Scope): Python =
  python(
    string(
      "(lambda ",
      0.until(arity).map { variable(scope.depth + it).python.string }.joinToString(", "),
      ":",
      expression.python(scope.iterate(arity) { push }).string,
      ")"))

fun ExpressionRecursive<Python>.python(scope: Scope): Python =
  python("Z(lambda ${variable(scope.depth).python.string}:${function.python(scope.push).string})")

fun ExpressionTuple<Python>.python(scope: Scope): Python =
  tuplePython(*expressionList.map { it.python(scope) }.toTypedArray())

fun ExpressionGet<Python>.python(scope: Scope): Python =
  lhs.python(scope).get(python(index))

fun Boolean.python(@Suppress("UNUSED_PARAMETER") scope: Scope): Python =
  python(this)

fun Int.python(@Suppress("UNUSED_PARAMETER") scope: Scope): Python =
  python(this)

fun ExpressionConditional<Python>.python(scope: Scope): Python =
  condition.python(scope).ifThenElse(trueCase.python(scope), falseCase.python(scope))

fun ExpressionSwitch<Python>.python(scope: Scope): Python =
  tuplePython(*cases.map { it.python(scope) }.toTypedArray())
    .get(lhs.python(scope))
    .invoke()

fun IndexVariable.python(scope: Scope) =
  variable(scope.depth - index - 1).python

val IndexVariable.python get() = Python("v${index}")