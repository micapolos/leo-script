package leo.named.compiler

import leo.Script
import leo.get
import leo.named.expression.Expression
import leo.named.typed.TypedExpression

val Script.typedExpression: TypedExpression get() =
	dictionary().typedExpressionCompilation(this).get(unitEnvironment)

val Script.expression: Expression get() =
	typedExpression.expression