package leo

import leo.base.fold

sealed class Expression
object EmptyExpression: Expression()
data class LinkExpression(val link: ExpressionLink): Expression()

data class ExpressionLink(val lhsExpression: Expression, val field: Op)

sealed class Op
data class AsOp(val as_: As): Op()
data class BeOp(val be: Be): Op()
data class CommentOp(val comment: Comment): Op()
data class DoOp(val do_: Do): Op()
data class FailOp(val fail: Fail): Op()
data class FieldOp(val field: OpField): Op()
data class LetOp(val let: Let): Op()
data class SetOp(val set: Set): Op()
data class SwitchOp(val switch: Switch): Op()
data class TryOp(val try_: Try): Op()
data class UpdateOp(val update: Update): Op()

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
data class Be(val expression: Expression)
data class Comment(val script: Script)
data class Do(val expression: Expression)
data class Fail(val expression: Expression)
data class Let(val pattern: Pattern, val rhs: LetRhs)
data class Set(val fieldStack: Stack<OpField>)
data class Try(val expression: Expression)
data class Update(val fieldStack: Stack<OpField>)

sealed class LetRhs
data class BeLetRhs(val be: Be): LetRhs()
data class DoLetRhs(val do_: Do): LetRhs()

fun expression(vararg ops: Op): Expression =
	(EmptyExpression as Expression).fold(ops) { op ->
		LinkExpression(ExpressionLink(this, op))
	}

fun switch(vararg cases: Case): Switch =
	(EmptySwitch as Switch).fold(cases) { case ->
		LinkSwitch(SwitchLink(this, case))
	}

fun op(name: String) = name opTo expression()
infix fun String.opTo(expression: Expression) = FieldOp(this fieldTo expression)
infix fun String.fieldTo(expression: Expression) = OpField(this, ExpressionOpFieldRhs(expression))
infix fun String.caseTo(expression: Expression) = Case(this, expression)

fun op(literal: Literal): Op =
	when (literal) {
		is NumberLiteral -> FieldOp(OpField(numberName, NativeOpFieldRhs(native(literal.number))))
		is StringLiteral -> FieldOp(OpField(textName, NativeOpFieldRhs(native(literal.string))))
	}

fun op(as_: As): Op = AsOp(as_)
fun op(be: Be): Op = BeOp(be)
fun op(comment: Comment): Op = CommentOp(comment)
fun op(do_: Do): Op = DoOp(do_)
fun op(fail: Fail): Op = FailOp(fail)
fun op(field: OpField): Op = FieldOp(field)
fun op(let: Let): Op = LetOp(let)
fun op(switch: Switch): Op = SwitchOp(switch)
fun op(set: Set): Op = SetOp(set)
fun op(try_: Try): Op = TryOp(try_)
fun op(update: Update): Op = UpdateOp(update)

fun as_(pattern: Pattern) = As(pattern)
fun be(expression: Expression) = Be(expression)
fun comment(script: Script) = Comment(script)
fun do_(expression: Expression) = Do(expression)
fun fail(expression: Expression) = Fail(expression)
fun let(pattern: Pattern, be: Be) = Let(pattern, BeLetRhs(be))
fun let(pattern: Pattern, do_: Do) = Let(pattern, DoLetRhs(do_))
fun set(vararg fields: OpField) = Set(stack(*fields))
fun try_(expression: Expression) = Try(expression)
fun update(vararg fields: OpField) = Update(stack(*fields))
