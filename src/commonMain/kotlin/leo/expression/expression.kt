package leo.expression

import leo.Literal
import leo.Stack
import leo.TypeLine
import leo.TypeStructure
import leo.isEmpty
import leo.lineTo
import leo.literal
import leo.map
import leo.numberTypeLine
import leo.onlyOrNull
import leo.push
import leo.stack
import leo.textTypeLine
import leo.type
import leo.typeLine

data class Expression(val op: Op, val typeLine: TypeLine)

sealed class Op
data class LiteralOp(val literal: Literal): Op()
data class GetOp(val get: Get): Op()
data class MakeOp(val make: Make): Op()
data class BindOp(val bind: Bind): Op()
data class VariableOp(val binding: Binding): Op()

data class Structure(val expressionStack: Stack<Expression>)

data class Get(val lhsExpression: Expression, val name: String)
data class Make(val lhsStructure: Structure, val name: String)
data class Bind(val lhsStructure: Structure, val rhsExpression: Expression)
data class Binding(val name: String)
data class Switch(val lhsExpression: Expression, val caseStack: Stack<Case>)
data class Case(val expression: Expression)

infix fun Op.of(typeLine: TypeLine) = Expression(this, typeLine)

fun op(literal: Literal): Op = LiteralOp(literal)
fun op(get: Get): Op = GetOp(get)

val Literal.op: Op get() = LiteralOp(this)
val Get.op: Op get() = GetOp(this)
val Make.op: Op get() = MakeOp(this)
val Bind.op: Op get() = BindOp(this)
val Binding.op: Op get() = VariableOp(this)

fun Expression.get(name: String) = Get(this, name)
fun Structure.make(name: String) = Make(this, name)
fun Structure.bind(expression: Expression) = Bind(this, expression)
val String.binding get() = Binding(this)

fun structure(vararg expressions: Expression) = Structure(stack(*expressions))
operator fun Structure.plus(expression: Expression) = Structure(expressionStack.push(expression))

fun Expression.switch(vararg cases: Case): Switch = Switch(this, stack(*cases))

val String.expression: Expression get() = op(literal(this)).of(textTypeLine)
val Double.expression: Expression get() = op(literal(this)).of(numberTypeLine)
val Int.expression: Expression get() = toDouble().expression

val Make.expression: Expression get() = op.of(name lineTo type(lhsStructure.typeStructure))
val Bind.expression: Expression get() = op.of(rhsExpression.typeLine)

val Expression.structure: Structure get() = structure(this)

val Structure.typeStructure: TypeStructure get() = TypeStructure(expressionStack.map { typeLine })
val Structure.isEmpty: Boolean get() = expressionStack.isEmpty

val String.structure: Structure get() = structure().make(this).op.of(this lineTo type()).structure

val Structure.expressionOrNull: Expression? get() = expressionStack.onlyOrNull

val Literal.expression: Expression get() = op of typeLine

infix fun String.expressionTo(structure: Structure): Expression =
	structure.make(this).op of (this lineTo type(expression.typeLine))