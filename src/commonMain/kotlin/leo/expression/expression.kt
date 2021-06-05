package leo.expression

import leo.Literal
import leo.Stack
import leo.push
import leo.stack

sealed class Expression
data class LiteralExpression(val literal: Literal): Expression()

data class VectorExpression(val vector: Vector): Expression()
data class AtExpression(val at: At): Expression()

data class ChoiceExpression(val choice: Choice): Expression()
data class SwitchExpression(val switch: Switch): Expression()

data class RepeatExpression(val repeat: Repeat): Expression()
data class EndExpression(val end: End): Expression()

data class VariableExpression(val variable: Variable): Expression()
data class FunctionExpression(val function: Function): Expression()
data class InvokeExpression(val invoke: Invoke): Expression()

data class Vector(val expressionStack: Stack<Expression>)
data class At(val vector: Expression, val indexExpression: Expression)

data class Choice(val index: Int, val expression: Expression)
data class Switch(val expression: Expression, val expressionStack: Stack<Expression>)

data class Variable(val index: Int)
data class Repeat(val expression: Expression)
object End

data class Function(val expression: Expression, val isRecursive: Boolean)
data class Invoke(val lhs: Expression, val rhs: Expression)

fun expression(): Expression = VectorExpression(Vector(stack()))
fun expression(literal: Literal): Expression = LiteralExpression(literal)

fun vector(vararg expressions: Expression) = Vector(stack(*expressions))
fun Vector.plus(expression: Expression): Vector = Vector(expressionStack.push(expression))
