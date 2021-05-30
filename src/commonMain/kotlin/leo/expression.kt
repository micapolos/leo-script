package leo

import leo.base.fold

sealed class Expression
object EmptyExpression: Expression()
data class LinkExpression(val link: ExpressionLink): Expression()

data class ExpressionLink(val lhsExpression: Expression, val field: Op)

sealed class Op
data class AsOp(val as_: As): Op()
data class CommentOp(val comment: Comment): Op()
data class FieldOp(val field: OpField): Op()
data class BindOp(val typed: Expression): Op()
data class InvokeOp(val typed: Expression): Op()
data class SwitchOp(val switch: Switch): Op()

data class OpField(val name: String, val rhsExpression: OpFieldRhs)

sealed class OpFieldRhs
data class NativeOpFieldRhs(val native: Native): OpFieldRhs()
data class FunctionOpFieldRhs(val function: Function): OpFieldRhs()
data class ExpressionOpFieldRhs(val typed: Expression): OpFieldRhs()

sealed class Switch
object EmptySwitch: Switch()
data class LinkSwitch(val link: SwitchLink): Switch()
data class SwitchLink(val lhsSwitch: Switch, val rhsCase: Case)
data class Case(val name: String, val expression: Expression)

data class As(val pattern: Pattern)
data class Comment(val script: Script)
data class LetDo(val expression: Expression)
data class LetBe(val expression: Expression)

fun expression(vararg ops: Op): Expression =
	(EmptyExpression as Expression).fold(ops) { op ->
		LinkExpression(ExpressionLink(this, op))
	}

fun switch(vararg cases: Case): Switch =
	(EmptySwitch as Switch).fold(cases) { case ->
		LinkSwitch(SwitchLink(this, case))
	}

infix fun String.fieldTo(expression: Expression) = FieldOp(OpField(this, ExpressionOpFieldRhs(expression)))
infix fun String.caseTo(expression: Expression) = Case(this, expression)

fun op(literal: Literal): Op =
	when (literal) {
		is NumberLiteral -> FieldOp(OpField(numberName, NativeOpFieldRhs(native(literal.number))))
		is StringLiteral -> FieldOp(OpField(textName, NativeOpFieldRhs(native(literal.string))))
	}

fun op(as_: As): Op = AsOp(as_)
fun op(comment: Comment): Op = CommentOp(comment)
fun op(switch: Switch): Op = SwitchOp(switch)

fun as_(pattern: Pattern) = As(pattern)
fun comment(script: Script) = Comment(script)