package leo.expression.compiler

import leo.Syntax

val Syntax.compiledExpression: CompiledExpression get() =
	compiler().compiledExpression(this)

val Syntax.compiledVector: CompiledVector get() =
	compiler().compiledVector(this)
