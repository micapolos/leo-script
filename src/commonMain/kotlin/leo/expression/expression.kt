package leo.expression

import leo.Literal
import leo.Stack

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
