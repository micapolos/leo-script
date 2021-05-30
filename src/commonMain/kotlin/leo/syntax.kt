package leo

data class Syntax(val syntaxLineStack: Stack<SyntaxLine>)

sealed class SyntaxLine
data class AsSyntaxLine(val as_: As): SyntaxLine()
data class BeSyntaxLine(val be: Be): SyntaxLine()
data class CommentSyntaxLine(val comment: Comment): SyntaxLine()
data class DoSyntaxLine(val do_: Do): SyntaxLine()
data class DoingSyntaxLine(val doing: Doing): SyntaxLine()
data class FailSyntaxLine(val fail: Fail): SyntaxLine()
data class FieldSyntaxLine(val field: SyntaxField): SyntaxLine()
data class GetSyntaxLine(val get: Get): SyntaxLine()
data class LetSyntaxLine(val let: Let): SyntaxLine()
data class MatchingSyntaxLine(val matching: Matching): SyntaxLine()
data class SetSyntaxLine(val set: Set): SyntaxLine()
data class SwitchSyntaxLine(val switch: Switch): SyntaxLine()
data class TrySyntaxLine(val try_: Try): SyntaxLine()
data class UpdateSyntaxLine(val update: Update): SyntaxLine()
data class UseSyntaxLine(val use: Use): SyntaxLine()
data class WithSyntaxLine(val with: With): SyntaxLine()

data class SyntaxField(val name: String, val rhsExpression: SyntaxRhs)

sealed class SyntaxRhs
data class NativeSyntaxRhs(val native: Native): SyntaxRhs()
data class FunctionSyntaxRhs(val function: Function): SyntaxRhs()
data class ExpressionSyntaxRhs(val typed: Syntax): SyntaxRhs()

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
data class Matching(val pattern: Pattern)
data class Not(val syntax: Syntax)
data class Set(val fieldStack: Stack<SyntaxField>)
data class Try(val syntax: Syntax)
data class Update(val fieldStack: Stack<SyntaxField>)
data class With(val syntax: Syntax)

sealed class LetRhs
data class BeLetRhs(val be: Be): LetRhs()
data class DoLetRhs(val do_: Do): LetRhs()

fun syntax(vararg syntaxLines: SyntaxLine): Syntax = Syntax(stack(*syntaxLines))
fun switch(vararg cases: Case): Switch = Switch(stack(*cases))

fun syntaxLine(name: String) = name lineTo syntax()
infix fun String.lineTo(syntax: Syntax) = FieldSyntaxLine(this fieldTo syntax)
infix fun String.fieldTo(syntax: Syntax) = SyntaxField(this, ExpressionSyntaxRhs(syntax))
infix fun String.caseDoing(syntax: Syntax) = Case(this, doing(syntax))

fun syntaxLine(literal: Literal): SyntaxLine =
	when (literal) {
		is NumberLiteral -> FieldSyntaxLine(SyntaxField(numberName, NativeSyntaxRhs(native(literal.number))))
		is StringLiteral -> FieldSyntaxLine(SyntaxField(textName, NativeSyntaxRhs(native(literal.string))))
	}

fun line(as_: As): SyntaxLine = AsSyntaxLine(as_)
fun line(be: Be): SyntaxLine = BeSyntaxLine(be)
fun line(comment: Comment): SyntaxLine = CommentSyntaxLine(comment)
fun line(do_: Do): SyntaxLine = DoSyntaxLine(do_)
fun line(doing: Doing): SyntaxLine = DoingSyntaxLine(doing)
fun line(fail: Fail): SyntaxLine = FailSyntaxLine(fail)
fun line(field: SyntaxField): SyntaxLine = FieldSyntaxLine(field)
fun line(get: Get): SyntaxLine = GetSyntaxLine(get)
fun line(let: Let): SyntaxLine = LetSyntaxLine(let)
fun line(matching: Matching): SyntaxLine = MatchingSyntaxLine(matching)
fun line(switch: Switch): SyntaxLine = SwitchSyntaxLine(switch)
fun line(set: Set): SyntaxLine = SetSyntaxLine(set)
fun line(try_: Try): SyntaxLine = TrySyntaxLine(try_)
fun line(update: Update): SyntaxLine = UpdateSyntaxLine(update)
fun line(use: Use): SyntaxLine = UseSyntaxLine(use)
fun line(with: With): SyntaxLine = WithSyntaxLine(with)

fun as_(pattern: Pattern) = As(pattern)
fun be(syntax: Syntax) = Be(syntax)
fun comment(script: Script) = Comment(script)
fun do_(syntax: Syntax) = Do(syntax)
fun doing(syntax: Syntax) = Doing(syntax)
fun fail(syntax: Syntax) = Fail(syntax)
fun get(vararg names: String) = Get(stack(*names))
fun matching(pattern: Pattern) = Matching(pattern)
fun not(syntax: Syntax) = Not(syntax)
fun let(pattern: Pattern, be: Be) = Let(pattern, BeLetRhs(be))
fun let(pattern: Pattern, do_: Do) = Let(pattern, DoLetRhs(do_))
fun set(vararg fields: SyntaxField) = Set(stack(*fields))
fun try_(syntax: Syntax) = Try(syntax)
fun update(vararg fields: SyntaxField) = Update(stack(*fields))
fun with(syntax: Syntax) = With(syntax)