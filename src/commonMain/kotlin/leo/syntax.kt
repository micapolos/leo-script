package leo

data class Syntax(val opStack: Stack<Op>)

sealed class Op
data class AsOp(val as_: As): Op()
data class BeOp(val be: Be): Op()
data class CommentOp(val comment: Comment): Op()
data class DoOp(val do_: Do): Op()
data class DoingOp(val doing: Doing): Op()
data class FailOp(val fail: Fail): Op()
data class FieldOp(val field: OpField): Op()
data class GetOp(val get: Get): Op()
data class LetOp(val let: Let): Op()
data class SetOp(val set: Set): Op()
data class SwitchOp(val switch: Switch): Op()
data class TryOp(val try_: Try): Op()
data class UpdateOp(val update: Update): Op()
data class UseOp(val use: Use): Op()
data class WithOp(val with: With): Op()

data class OpField(val name: String, val rhsExpression: OpFieldRhs)

sealed class OpFieldRhs
data class NativeOpFieldRhs(val native: Native): OpFieldRhs()
data class FunctionOpFieldRhs(val function: Function): OpFieldRhs()
data class ExpressionOpFieldRhs(val typed: Syntax): OpFieldRhs()

data class Switch(val caseStack: Stack<Case>)
data class Case(val name: String, val doing: Doing)

data class As(val pattern: Pattern)
data class Be(val syntax: Syntax)
data class Comment(val script: Script)
data class Do(val syntax: Syntax)
data class Doing(val syntax: Syntax)
data class Fail(val syntax: Syntax)
data class Get(val nameStack: Stack<String>)
data class Let(val pattern: Pattern, val rhs: LetRhs)
data class Not(val syntax: Syntax)
data class Set(val fieldStack: Stack<OpField>)
data class Try(val syntax: Syntax)
data class Update(val fieldStack: Stack<OpField>)
data class With(val syntax: Syntax)

sealed class LetRhs
data class BeLetRhs(val be: Be): LetRhs()
data class DoLetRhs(val do_: Do): LetRhs()

fun syntax(vararg ops: Op): Syntax = Syntax(stack(*ops))
fun switch(vararg cases: Case): Switch = Switch(stack(*cases))

fun op(name: String) = name opTo syntax()
infix fun String.opTo(syntax: Syntax) = FieldOp(this fieldTo syntax)
infix fun String.fieldTo(syntax: Syntax) = OpField(this, ExpressionOpFieldRhs(syntax))
infix fun String.caseDoing(syntax: Syntax) = Case(this, doing(syntax))

fun op(literal: Literal): Op =
	when (literal) {
		is NumberLiteral -> FieldOp(OpField(numberName, NativeOpFieldRhs(native(literal.number))))
		is StringLiteral -> FieldOp(OpField(textName, NativeOpFieldRhs(native(literal.string))))
	}

fun op(as_: As): Op = AsOp(as_)
fun op(be: Be): Op = BeOp(be)
fun op(comment: Comment): Op = CommentOp(comment)
fun op(do_: Do): Op = DoOp(do_)
fun op(doing: Doing): Op = DoingOp(doing)
fun op(fail: Fail): Op = FailOp(fail)
fun op(field: OpField): Op = FieldOp(field)
fun op(get: Get): Op = GetOp(get)
fun op(let: Let): Op = LetOp(let)
fun op(switch: Switch): Op = SwitchOp(switch)
fun op(set: Set): Op = SetOp(set)
fun op(try_: Try): Op = TryOp(try_)
fun op(update: Update): Op = UpdateOp(update)
fun op(use: Use): Op = UseOp(use)
fun op(with: With): Op = WithOp(with)

fun as_(pattern: Pattern) = As(pattern)
fun be(syntax: Syntax) = Be(syntax)
fun comment(script: Script) = Comment(script)
fun do_(syntax: Syntax) = Do(syntax)
fun doing(syntax: Syntax) = Doing(syntax)
fun fail(syntax: Syntax) = Fail(syntax)
fun get(vararg names: String) = Get(stack(*names))
fun not(syntax: Syntax) = Not(syntax)
fun let(pattern: Pattern, be: Be) = Let(pattern, BeLetRhs(be))
fun let(pattern: Pattern, do_: Do) = Let(pattern, DoLetRhs(do_))
fun set(vararg fields: OpField) = Set(stack(*fields))
fun try_(syntax: Syntax) = Try(syntax)
fun update(vararg fields: OpField) = Update(stack(*fields))
fun with(syntax: Syntax) = With(syntax)