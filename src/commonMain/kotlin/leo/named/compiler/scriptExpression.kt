package leo.named.compiler

import leo.Script
import leo.get
import leo.map
import leo.named.expression.Expression
import leo.named.expression.Line
import leo.named.typed.TypedLine

val Script.line: Line<Unit> get() =
	unitEnvironment.context.typedExpressionCompilation(this).map { it.line }.get(unitEnvironment.context)

val Script.typedLine: TypedLine<Unit> get() =
	unitEnvironment.context.typedExpressionCompilation(this).get(unitEnvironment.context)

val Script.expression: Expression<Unit> get() =
	unitEnvironment.context.typedStructureCompilation(this).map { it.expression }.get(unitEnvironment.context)