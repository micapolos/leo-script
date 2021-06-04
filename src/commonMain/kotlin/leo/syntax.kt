package leo

import leo.base.negate

data class Syntax(val lineStack: Stack<SyntaxLine>)

sealed class SyntaxLine
data class AnySyntaxLine(val any: SyntaxAny): SyntaxLine()
data class AsSyntaxLine(val as_: As): SyntaxLine()
data class BeSyntaxLine(val be: Be): SyntaxLine()
data class BindSyntaxLine(val bind: Bind): SyntaxLine()
data class BreakSyntaxLine(val break_: Break): SyntaxLine()
data class CheckSyntaxLine(val check: Check): SyntaxLine()
data class CommentSyntaxLine(val comment: Comment): SyntaxLine()
data class DoSyntaxLine(val do_: Do): SyntaxLine()
data class DoingSyntaxLine(val doing: Doing): SyntaxLine()
data class ExampleSyntaxLine(val example: Example): SyntaxLine()
data class FailSyntaxLine(val fail: Fail): SyntaxLine()
data class AtomSyntaxLine(val atom: SyntaxAtom): SyntaxLine()
data class GetSyntaxLine(val get: Get): SyntaxLine()
data class GiveSyntaxLine(val give: Give): SyntaxLine()
data class IsSyntaxLine(val is_: Is): SyntaxLine()
data class LetSyntaxLine(val let: Let): SyntaxLine()
data class LoopSyntaxLine(val loop: Loop): SyntaxLine()
data class MatchingSyntaxLine(val matching: Matching): SyntaxLine()
data class PrivateSyntaxLine(val private: Private): SyntaxLine()
data class RecurseSyntaxLine(val recurse: Recurse): SyntaxLine()
data class QuoteSyntaxLine(val quote: Quote): SyntaxLine()
data class SetSyntaxLine(val set: Set): SyntaxLine()
data class SwitchSyntaxLine(val switch: Switch): SyntaxLine()
data class TakeSyntaxLine(val take: Take): SyntaxLine()
data class TestSyntaxLine(val test: Test): SyntaxLine()
data class TrySyntaxLine(val try_: Try): SyntaxLine()
data class UpdateSyntaxLine(val update: Update): SyntaxLine()
data class UseSyntaxLine(val use: Use): SyntaxLine()
data class WithSyntaxLine(val with: With): SyntaxLine()

data class SyntaxField(val name: String, val rhsSyntax: Syntax)

sealed class SyntaxAtom
data class FieldSyntaxAtom(val field: SyntaxField): SyntaxAtom()
data class LiteralSyntaxAtom(val literal: Literal): SyntaxAtom()

data class Switch(val caseStack: Stack<Case>)
data class Case(val name: String, val syntax: Syntax)

object SyntaxAny
data class As(val syntax: Syntax)
data class Be(val syntax: Syntax)
data class Bind(val syntax: Syntax)
data class Break(val syntax: Syntax)
data class Check(val is_: Is)
data class Comment(val script: Script)
data class Do(val block: Block)
data class Doing(val block: Block)
data class Equal(val syntax: Syntax)
data class Example(val syntax: Syntax)
data class Fail(val syntax: Syntax)
data class Get(val nameStack: Stack<String>)
data class Give(val syntax: Syntax)
data class Is(val rhs: IsRhs, val negated: Boolean)
data class Let(val syntax: Syntax, val rhs: LetRhs)
data class Loop(val syntax: Syntax)
data class Matching(val syntax: Syntax)
data class Not(val syntax: Syntax)
data class Private(val syntax: Syntax)
data class Recurse(val syntax: Syntax)
data class Recursing(val syntax: Syntax)
data class Quote(val script: Script)
data class Set(val atomStack: Stack<SyntaxAtom>)
data class Take(val syntax: Syntax)
data class Test(val lhsSyntax: Syntax, val is_: Is)
data class Try(val syntax: Syntax)
data class Update(val fieldStack: Stack<SyntaxField>)
data class With(val syntax: Syntax)

sealed class LetRhs
data class BeLetRhs(val be: Be): LetRhs()
data class DoLetRhs(val do_: Do): LetRhs()

sealed class Block
data class SyntaxBlock(val syntax: Syntax): Block()
data class RecursingBlock(val recursing: Recursing): Block()

sealed class IsRhs
data class SyntaxIsRhs(val syntax: Syntax): IsRhs()
data class EqualIsRhs(val equal: Equal): IsRhs()
data class MatchingIsRhs(val matching: Matching): IsRhs()

val Get.nameSeq get() = nameStack.reverse.seq
val Syntax.lineSeq get() = lineStack.reverse.seq
val Set.atomSeq get() = atomStack.reverse.seq
val Switch.caseSeq get() = caseStack.reverse.seq
val Update.fieldSeq get() = fieldStack.reverse.seq

fun syntax(name: String) = syntax(name lineTo syntax())
fun syntax(vararg syntaxLines: SyntaxLine): Syntax = Syntax(stack(*syntaxLines))
fun Syntax.plus(line: SyntaxLine): Syntax = Syntax(lineStack.push(line))
fun switch(vararg cases: Case): Switch = Switch(stack(*cases))

fun syntaxLine(name: String) = name lineTo syntax()
infix fun String.lineTo(syntax: Syntax) = line(atom(this fieldTo syntax))
infix fun String.fieldTo(syntax: Syntax) = SyntaxField(this, syntax)
infix fun String.caseTo(syntax: Syntax) = Case(this, syntax)

fun line(as_: As): SyntaxLine = AsSyntaxLine(as_)
fun line(any: SyntaxAny): SyntaxLine = AnySyntaxLine(any)
fun line(be: Be): SyntaxLine = BeSyntaxLine(be)
fun line(bind: Bind): SyntaxLine = BindSyntaxLine(bind)
fun line(break_: Break): SyntaxLine = BreakSyntaxLine(break_)
fun line(check: Check): SyntaxLine = CheckSyntaxLine(check)
fun line(comment: Comment): SyntaxLine = CommentSyntaxLine(comment)
fun line(do_: Do): SyntaxLine = DoSyntaxLine(do_)
fun line(doing: Doing): SyntaxLine = DoingSyntaxLine(doing)
fun line(example: Example): SyntaxLine = ExampleSyntaxLine(example)
fun line(fail: Fail): SyntaxLine = FailSyntaxLine(fail)
fun line(field: SyntaxField): SyntaxLine = line(atom(field))
fun line(get: Get): SyntaxLine = GetSyntaxLine(get)
fun line(give: Give): SyntaxLine = GiveSyntaxLine(give)
fun line(is_: Is): SyntaxLine = IsSyntaxLine(is_)
fun line(let: Let): SyntaxLine = LetSyntaxLine(let)
fun line(loop: Loop): SyntaxLine = LoopSyntaxLine(loop)
fun syntaxLine(literal: Literal): SyntaxLine = line(syntaxAtom(literal))
fun line(atom: SyntaxAtom): SyntaxLine = AtomSyntaxLine(atom)
fun line(matching: Matching): SyntaxLine = MatchingSyntaxLine(matching)
fun line(switch: Switch): SyntaxLine = SwitchSyntaxLine(switch)
fun line(private: Private): SyntaxLine = PrivateSyntaxLine(private)
fun line(recurse: Recurse): SyntaxLine = RecurseSyntaxLine(recurse)
fun line(quote: Quote): SyntaxLine = QuoteSyntaxLine(quote)
fun line(set: Set): SyntaxLine = SetSyntaxLine(set)
fun line(take: Take): SyntaxLine = TakeSyntaxLine(take)
fun line(test: Test): SyntaxLine = TestSyntaxLine(test)
fun line(try_: Try): SyntaxLine = TrySyntaxLine(try_)
fun line(update: Update): SyntaxLine = UpdateSyntaxLine(update)
fun line(use: Use): SyntaxLine = UseSyntaxLine(use)
fun line(with: With): SyntaxLine = WithSyntaxLine(with)

fun as_(syntax: Syntax) = As(syntax)
fun any() = SyntaxAny
fun be(syntax: Syntax) = Be(syntax)
fun bind(syntax: Syntax) = Bind(syntax)
fun break_(syntax: Syntax) = Break(syntax)
fun check(is_: Is) = Check(is_)
fun comment(script: Script) = Comment(script)
fun do_(block: Block) = Do(block)
fun doing(block: Block) = Doing(block)
fun equal(syntax: Syntax) = Equal(syntax)
fun example(syntax: Syntax) = Example(syntax)
fun fail(syntax: Syntax) = Fail(syntax)
fun get(vararg names: String) = Get(stack(*names))
fun give(syntax: Syntax) = Give(syntax)
fun is_(rhs: IsRhs) = Is(rhs, negated = false)
fun matching(syntax: Syntax) = Matching(syntax)
fun not(syntax: Syntax) = Not(syntax)
fun let(syntax: Syntax, be: Be) = Let(syntax, BeLetRhs(be))
fun let(syntax: Syntax, do_: Do) = Let(syntax, DoLetRhs(do_))
fun loop(syntax: Syntax) = Loop(syntax)
fun private(syntax: Syntax) = Private(syntax)
fun recurse(syntax: Syntax) = Recurse(syntax)
fun recursing(syntax: Syntax) = Recursing(syntax)
fun quote(script: Script) = Quote(script)
fun set(vararg atoms: SyntaxAtom) = Set(stack(*atoms))
fun take(syntax: Syntax) = Take(syntax)
fun test(syntax: Syntax, is_: Is) = Test(syntax, is_)
fun try_(syntax: Syntax) = Try(syntax)
fun update(vararg fields: SyntaxField) = Update(stack(*fields))
fun with(syntax: Syntax) = With(syntax)

fun atom(field: SyntaxField): SyntaxAtom = FieldSyntaxAtom(field)
fun syntaxAtom(literal: Literal): SyntaxAtom = LiteralSyntaxAtom(literal)

fun isRhs(syntax: Syntax): IsRhs = SyntaxIsRhs(syntax)
fun isRhs(equal: Equal): IsRhs = EqualIsRhs(equal)
fun isRhs(matching: Matching): IsRhs = MatchingIsRhs(matching)

fun block(syntax: Syntax): Block = SyntaxBlock(syntax)
fun block(recursing: Recursing): Block = RecursingBlock(recursing)

val Is.negate get() = copy(negated = negated.negate)

val Test.syntax get() = lhsSyntax.plus(line(is_))

val Syntax.isEmpty get() = lineStack.isEmpty