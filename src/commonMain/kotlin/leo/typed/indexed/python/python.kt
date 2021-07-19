package leo.typed.indexed.python

import leo.Empty
import leo.IndexVariable
import leo.base.iterate
import leo.base.string
import leo.typed.compiled.Scope
import leo.typed.compiled.push
import leo.typed.compiled.scope
import leo.typed.compiler.python.Python
import leo.typed.compiler.python.get
import leo.typed.compiler.python.ifThenElse
import leo.typed.compiler.python.invoke
import leo.typed.compiler.python.python
import leo.typed.compiler.python.tuplePython
import leo.typed.indexed.BooleanExpression
import leo.typed.indexed.ConditionalExpression
import leo.typed.indexed.EmptyExpression
import leo.typed.indexed.Expression
import leo.typed.indexed.ExpressionConditional
import leo.typed.indexed.ExpressionFunction
import leo.typed.indexed.ExpressionGet
import leo.typed.indexed.ExpressionInvoke
import leo.typed.indexed.ExpressionRecursive
import leo.typed.indexed.ExpressionSwitch
import leo.typed.indexed.ExpressionTuple
import leo.typed.indexed.FunctionExpression
import leo.typed.indexed.GetExpression
import leo.typed.indexed.IndexExpression
import leo.typed.indexed.InvokeExpression
import leo.typed.indexed.NativeExpression
import leo.typed.indexed.RecursiveExpression
import leo.typed.indexed.SwitchExpression
import leo.typed.indexed.TupleExpression
import leo.typed.indexed.VariableExpression
import leo.typed.indexed.containsRecursion
import leo.variable

val Expression<Python>.python: Python get() =
  python(
    string(
      "import operator;",
      if (containsRecursion) "Z=lambda f:(lambda g:f(g(g)))(lambda g:f(lambda *y:g(g)(*y)));" else "",
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