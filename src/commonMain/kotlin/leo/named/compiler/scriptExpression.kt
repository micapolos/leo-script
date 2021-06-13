package leo.named.compiler

import leo.Script
import leo.get
import leo.named.expression.Expression
import leo.named.typed.TypedExpression

val Script.typedExpression: TypedExpression<Unit> get() =
	unitEnvironment.context.typedExpressionCompilation(this).get(unitEnvironment.context)

val Script.expression: Expression<Unit> get() =
	typedExpression.expression