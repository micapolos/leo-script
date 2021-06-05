package leo.expression.compiler

import leo.Type
import leo.TypeLine
import leo.TypeStructure
import leo.expression.Expression
import leo.expression.Vector
import leo.expression.plus
import leo.expression.vector
import leo.isStatic
import leo.plus

data class Compiled<V, T>(val v: V, val t: T)
fun <V, T> V.of(t: T) = Compiled(this, t)

typealias CompiledVector = Compiled<Vector, TypeStructure>
typealias CompiledExpression = Compiled<Expression, TypeLine>

val Compiled<Expression, *>.expression get() = v
val Compiled<Vector, *>.vector get() = v

val Compiled<*, Type>.type get() = t
val Compiled<*, TypeLine>.typeLine get() = t
val Compiled<*, TypeStructure>.typeStructure get() = t

fun CompiledVector.vectorPlusExpressionVector(rhs: CompiledExpression): Vector =
	if (typeStructure.isStatic)
		if (rhs.typeLine.isStatic) vector()
		else vector().plus(rhs.expression)
  else
		if (rhs.typeLine.isStatic) vector
		else vector.plus(rhs.expression)

fun CompiledVector.vectorPlusExpression(rhs: CompiledExpression): CompiledVector =
	vectorPlusExpressionVector(rhs).of(typeStructure.plus(rhs.typeLine))
