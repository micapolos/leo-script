package leo.named.compiler

import leo.Script
import leo.get
import leo.map
import leo.named.expression.Expression
import leo.named.expression.Structure

val Script.expression: Expression<Unit> get() =
	unitEnvironment.context.typedExpressionCompilation(this).map { it.expression }.get(unitEnvironment.context)

val Script.structure: Structure<Unit> get() =
	unitEnvironment.context.typedStructureCompilation(this).map { it.structure }.get(unitEnvironment.context)