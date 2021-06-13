package leo.named.compiler

import leo.Stack
import leo.named.expression.Expression
import leo.push
import leo.stack

data class Scope(val expressionStack: Stack<Expression>)
fun scope(vararg expressions: Expression) = Scope(stack(*expressions))
fun Scope.plus(expression: Expression) = expressionStack.push(expression).let(::Scope)