package leo.named.compiler

import leo.Script
import leo.get
import leo.map
import leo.named.expression.Line
import leo.named.expression.Structure
import leo.named.typed.TypedExpression

val Script.line: Line<Unit> get() =
	unitEnvironment.context.typedExpressionCompilation(this).map { it.line }.get(unitEnvironment.context)

val Script.typedExpression: TypedExpression<Unit> get() =
	unitEnvironment.context.typedExpressionCompilation(this).get(unitEnvironment.context)

val Script.structure: Structure<Unit> get() =
	unitEnvironment.context.typedStructureCompilation(this).map { it.structure }.get(unitEnvironment.context)