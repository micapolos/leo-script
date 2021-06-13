package leo.named.compiler

import leo.Stack
import leo.named.typed.TypedExpression
import leo.push
import leo.stack

data class Scope(val typedExpressionStack: Stack<TypedExpression>)
fun scope(vararg typedExpressions: TypedExpression) = Scope(stack(*typedExpressions))
fun Scope.plus(typedExpression: TypedExpression) = typedExpressionStack.push(typedExpression).let(::Scope)