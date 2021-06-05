package leo.expression.compiler

import leo.Syntax
import leo.get

data class Compiler(val unit: Unit)

fun compiler() = Compiler(Unit)

fun Compiler.compiledExpression(syntax: Syntax): CompiledExpression =
	syntax.expressionCompilation.get(this)

fun Compiler.compiledVector(syntax: Syntax): CompiledVector =
	syntax.vectorCompilation.get(this)
