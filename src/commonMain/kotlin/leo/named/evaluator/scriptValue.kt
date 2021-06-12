package leo.named.evaluator

import leo.Script
import leo.get
import leo.named.compiler.expression
import leo.named.compiler.line
import leo.named.value.Structure
import leo.named.value.Value

val Script.structure: Structure<Unit> get() =
	expression.structureEvaluation.get(dictionary())

val Script.value: Value<Unit> get() =
	line.valueEvaluation.get(dictionary())
