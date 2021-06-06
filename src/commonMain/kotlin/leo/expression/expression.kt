package leo.expression

import leo.Literal
import leo.Stack
import leo.TypeLine
import leo.TypeStructure
import leo.lineTo
import leo.literal
import leo.map
import leo.numberTypeLine
import leo.push
import leo.stack
import leo.textTypeLine
import leo.type

data class Expression(val op: Op, val typeLine: TypeLine)

sealed class Op
data class LiteralOp(val literal: Literal): Op()
data class GetOp(val get: Get): Op()
data class MakeOp(val make: Make): Op()

data class Structure(val expressionStack: Stack<Expression>)

data class Get(val lhsExpression: Expression, val name: String)
data class Make(val lhsStructure: Structure, val name: String)

infix fun Op.of(typeLine: TypeLine) = Expression(this, typeLine)

fun op(literal: Literal): Op = LiteralOp(literal)
fun op(get: Get): Op = GetOp(get)

val Literal.op: Op get() = LiteralOp(this)
val Get.op: Op get() = GetOp(this)
val Make.op: Op get() = MakeOp(this)

fun Expression.get(name: String) = Get(this, name)
fun Structure.make(name: String) = Make(this, name)

val Expression.structure get() = structure(this)
fun structure(vararg expressions: Expression) = Structure(stack(*expressions))
operator fun Structure.plus(expression: Expression) = Structure(expressionStack.push(expression))

val String.expression: Expression get() = op(literal(this)).of(textTypeLine)
val Int.expression: Expression get() = op(literal(this)).of(numberTypeLine)

val Make.expression: Expression get() =
	op.of(name lineTo type(lhsStructure.typeStructure))

val Structure.typeStructure get() =
	TypeStructure(expressionStack.map { typeLine })
