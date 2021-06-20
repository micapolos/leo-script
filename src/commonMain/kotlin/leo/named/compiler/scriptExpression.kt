package leo.named.compiler

import leo.Script
import leo.get
import leo.map
import leo.named.expression.Expression
import leo.named.typed.TypedExpression

val Script.typedExpression: TypedExpression get() =
	dictionary().typedExpressionCompilation(this).get(unitEnvironment)

val Script.expression: Expression get() =
	typedExpression.expression

fun Dictionary.expression(script: Script): Expression =
	module.compiler.plusCompilation(script).map { it.typedExpression.expression }.get(unitEnvironment)

fun Script.typedExpression(dictionary: Dictionary): TypedExpression =
	dictionary.module.compiler.plusCompilation(this).map { it.typedExpression }.get(unitEnvironment)

fun Script.typedExpression(compiler: Compiler): TypedExpression =
	compiler.plusCompilation(this).map { it.typedExpression }.get(unitEnvironment)

