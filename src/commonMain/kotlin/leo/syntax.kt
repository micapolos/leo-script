package leo

import leo.base.negate

data class Syntax(val lineStack: Stack<SyntaxLine>)

sealed class SyntaxLine
data class AsSyntaxLine(val as_: As): SyntaxLine()
data class BeSyntaxLine(val be: Be): SyntaxLine()
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
data class MatchingSyntaxLine(val matching: Matching): SyntaxLine()
data class PrivateSyntaxLine(val private: Private): SyntaxLine()
data class RecurseSyntaxLine(val recurse: Recurse): SyntaxLine()
data class RepeatSyntaxLine(val repeat: Repeat): SyntaxLine()
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
data class Case(val name: String, val doing: Doing)

data class As(val pattern: Pattern)
data class Be(val syntax: Syntax)
data class Comment(val script: Script)
data class Do(val block: SyntaxBlock)
data class Doing(val block: SyntaxBlock)
data class Equal(val syntax: Syntax)
data class Example(val syntax: Syntax)
data class Fail(val syntax: Syntax)
data class Get(val nameStack: Stack<String>)
data class Give(val syntax: Syntax)
data class Is(val rhs: IsRhs, val negated: Boolean)
data class Let(val pattern: Pattern, val rhs: LetRhs)
data class Matching(val pattern: Pattern)
data class Not(val syntax: Syntax)
data class Private(val syntax: Syntax)
data class Repeat(val syntax: Syntax)
data class Recurse(val syntax: Syntax)
data class Quote(val script: Script)
data class Set(val atomStack: Stack<SyntaxAtom>)
data class SyntaxBlock(val typeOrNull: BlockType?, val syntax: Syntax)
data class Take(val syntax: Syntax)
data class Test(val syntax: Syntax, val is_: Is)
data class Try(val syntax: Syntax)
data class Update(val fieldStack: Stack<SyntaxField>)
data class With(val syntax: Syntax)

sealed class LetRhs
data class BeLetRhs(val be: Be): LetRhs()
data class DoLetRhs(val do_: Do): LetRhs()

sealed class IsRhs
data class SyntaxIsRhs(val syntax: Syntax): IsRhs()
data class EqualIsRhs(val equal: Equal): IsRhs()
data class MatchingIsRhs(val matching: Matching): IsRhs()

val Get.nameSeq get() = nameStack.reverse.seq
val Syntax.lineSeq get() = lineStack.reverse.seq
val Set.atomSeq get() = atomStack.reverse.seq
val Switch.caseSeq get() = caseStack.reverse.seq
val Update.fieldSeq get() = fieldStack.reverse.seq

fun syntax(vararg syntaxLines: SyntaxLine): Syntax = Syntax(stack(*syntaxLines))
fun Syntax.plus(line: SyntaxLine): Syntax = Syntax(lineStack.push(line))
fun switch(vararg cases: Case): Switch = Switch(stack(*cases))

fun syntaxLine(name: String) = name lineTo syntax()
infix fun String.lineTo(syntax: Syntax) = line(atom(this fieldTo syntax))
infix fun String.fieldTo(syntax: Syntax) = SyntaxField(this, syntax)
infix fun String.caseDoing(block: SyntaxBlock) = Case(this, doing(block))
infix fun String.caseDoing(syntax: Syntax) = this caseDoing block(null, syntax)

fun line(as_: As): SyntaxLine = AsSyntaxLine(as_)
fun line(be: Be): SyntaxLine = BeSyntaxLine(be)
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
fun syntaxLine(literal: Literal): SyntaxLine = line(atom2(literal))
fun line(atom: SyntaxAtom): SyntaxLine = AtomSyntaxLine(atom)
fun line(matching: Matching): SyntaxLine = MatchingSyntaxLine(matching)
fun line(switch: Switch): SyntaxLine = SwitchSyntaxLine(switch)
fun line(private: Private): SyntaxLine = PrivateSyntaxLine(private)
fun line(recurse: Recurse): SyntaxLine = RecurseSyntaxLine(recurse)
fun line(repeat: Repeat): SyntaxLine = RepeatSyntaxLine(repeat)
fun line(quote: Quote): SyntaxLine = QuoteSyntaxLine(quote)
fun line(set: Set): SyntaxLine = SetSyntaxLine(set)
fun line(take: Take): SyntaxLine = TakeSyntaxLine(take)
fun line(test: Test): SyntaxLine = TestSyntaxLine(test)
fun line(try_: Try): SyntaxLine = TrySyntaxLine(try_)
fun line(update: Update): SyntaxLine = UpdateSyntaxLine(update)
fun line(use: Use): SyntaxLine = UseSyntaxLine(use)
fun line(with: With): SyntaxLine = WithSyntaxLine(with)

fun as_(pattern: Pattern) = As(pattern)
fun be(syntax: Syntax) = Be(syntax)
fun block(typeOrNull: BlockType?, syntax: Syntax) = SyntaxBlock(typeOrNull, syntax)
fun block(syntax: Syntax) = block(null, syntax)
fun comment(script: Script) = Comment(script)
fun do_(block: SyntaxBlock) = Do(block)
fun doing(block: SyntaxBlock) = Doing(block)
fun equal(syntax: Syntax) = Equal(syntax)
fun example(syntax: Syntax) = Example(syntax)
fun fail(syntax: Syntax) = Fail(syntax)
fun get(vararg names: String) = Get(stack(*names))
fun give(syntax: Syntax) = Give(syntax)
fun is_(rhs: IsRhs) = Is(rhs, negated = false)
fun matching(pattern: Pattern) = Matching(pattern)
fun not(syntax: Syntax) = Not(syntax)
fun let(pattern: Pattern, be: Be) = Let(pattern, BeLetRhs(be))
fun let(pattern: Pattern, do_: Do) = Let(pattern, DoLetRhs(do_))
fun private(syntax: Syntax) = Private(syntax)
fun recurse(syntax: Syntax) = Recurse(syntax)
fun repeat(syntax: Syntax) = Repeat(syntax)
fun quote(script: Script) = Quote(script)
fun set(vararg atoms: SyntaxAtom) = Set(stack(*atoms))
fun take(syntax: Syntax) = Take(syntax)
fun test(syntax: Syntax, is_: Is) = Test(syntax, is_)
fun try_(syntax: Syntax) = Try(syntax)
fun update(vararg fields: SyntaxField) = Update(stack(*fields))
fun with(syntax: Syntax) = With(syntax)

fun atom(field: SyntaxField): SyntaxAtom = FieldSyntaxAtom(field)
fun atom2(literal: Literal): SyntaxAtom = LiteralSyntaxAtom(literal)

fun isRhs(syntax: Syntax): IsRhs = SyntaxIsRhs(syntax)
fun isRhs(equal: Equal): IsRhs = EqualIsRhs(equal)
fun isRhs(matching: Matching): IsRhs = MatchingIsRhs(matching)

val Is.negate get() = copy(negated = negated.negate)

val IsRhs.syntax get() =
	when (this) {
		is EqualIsRhs -> equal.syntax
		is MatchingIsRhs -> matching.pattern
		is SyntaxIsRhs -> TODO()
	}