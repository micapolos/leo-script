package leo

import leo.base.negate

data class Syntax(val lineStack: Stack<SyntaxLine>)

sealed class SyntaxLine
data class ApplySyntaxLine(val apply: Apply) : SyntaxLine()
data class ApplyingSyntaxLine(val applying: Applying) : SyntaxLine()
data class AsSyntaxLine(val as_: As) : SyntaxLine()

//data class BeSyntaxLine(val be: Be): SyntaxLine()
data class BeingSyntaxLine(val being: Being) : SyntaxLine()
data class CheckSyntaxLine(val check: Check) : SyntaxLine()
data class CombineWithSyntaxLine(val combineWith: CombineWith) : SyntaxLine()
data class CombiningWithSyntaxLine(val combiningWith: CombiningWith) : SyntaxLine()
data class CommentSyntaxLine(val comment: Comment) : SyntaxLine()
data class DebugSyntaxLine(val debug: Debug) : SyntaxLine()
data class DoSyntaxLine(val do_: Do) : SyntaxLine()
data class DoingSyntaxLine(val doing: Doing) : SyntaxLine()
data class EndSyntaxLine(val end: End) : SyntaxLine()

//data class ExampleSyntaxLine(val example: Example): SyntaxLine()
//data class FailSyntaxLine(val fail: Fail): SyntaxLine()
data class AtomSyntaxLine(val atom: SyntaxAtom) : SyntaxLine()
data class GetSyntaxLine(val get: Get) : SyntaxLine()

//data class GiveSyntaxLine(val give: Give): SyntaxLine()
data class HelpSyntaxLine(val help: Help) : SyntaxLine()
data class IsSyntaxLine(val is_: Is) : SyntaxLine()
data class LetSyntaxLine(val let: Let) : SyntaxLine()
data class LoadSyntaxLine(val load: Load) : SyntaxLine()
data class MatchingSyntaxLine(val matching: Matching) : SyntaxLine()
data class PrivateSyntaxLine(val private: Private) : SyntaxLine()
data class RecurseSyntaxLine(val recurse: Recurse) : SyntaxLine()
data class RecursiveSyntaxLine(val recursive: Recursive) : SyntaxLine()
data class RepeatSyntaxLine(val repeat: Repeat) : SyntaxLine()
data class QuoteSyntaxLine(val quote: Quote) : SyntaxLine()
data class SetSyntaxLine(val set: Set) : SyntaxLine()
data class SelectSyntaxLine(val select: Select) : SyntaxLine()

//data class TakeSyntaxLine(val take: Take): SyntaxLine()
data class TheSyntaxLine(val the: The) : SyntaxLine()
data class TestSyntaxLine(val test: Test) : SyntaxLine()
data class TrySyntaxLine(val try_: Try) : SyntaxLine()
data class UpdateSyntaxLine(val update: Update) : SyntaxLine()
data class UseSyntaxLine(val use: Use) : SyntaxLine()
//data class WithSyntaxLine(val with: With): SyntaxLine()

data class SyntaxField(val name: String, val rhsSyntax: Syntax)

sealed class SyntaxAtom
data class FieldSyntaxAtom(val field: SyntaxField) : SyntaxAtom()
data class LiteralSyntaxAtom(val literal: Literal) : SyntaxAtom()

data class Select(val caseStack: Stack<Case>)
data class Case(val name: String, val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Apply(val block: Block)

@kotlin.jvm.JvmInline
value class Applying(val block: Block)

@kotlin.jvm.JvmInline
value class As(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Be(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Being(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Check(val is_: Is)

@kotlin.jvm.JvmInline
value class Comment(val script: Script)

@kotlin.jvm.JvmInline
value class CombineWith(val block: Block)

@kotlin.jvm.JvmInline
value class CombiningWith(val block: Block)

@kotlin.jvm.JvmInline
value class Debug(val script: Script)

@kotlin.jvm.JvmInline
value class Do(val block: Block)

@kotlin.jvm.JvmInline
value class Doing(val block: Block)

@kotlin.jvm.JvmInline
value class End(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Equal(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Example(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Fail(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Get(val nameStack: Stack<String>)

@kotlin.jvm.JvmInline
value class Give(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Have(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Help(val script: Script)
data class Is(val rhs: IsRhs, val negated: Boolean)
data class Let(val syntax: Syntax, val rhs: LetRhs)

@kotlin.jvm.JvmInline
value class Load(val nameStackLink: StackLink<String>)

@kotlin.jvm.JvmInline
value class Repeat(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Matching(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Not(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Private(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Recurse(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Recursing(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Recursive(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Quote(val script: Script)

@kotlin.jvm.JvmInline
value class Set(val atomStack: Stack<SyntaxAtom>)

@kotlin.jvm.JvmInline
value class Take(val syntax: Syntax)
data class Test(val lhsSyntax: Syntax, val is_: Is)

@kotlin.jvm.JvmInline
value class The(val atomStack: Stack<SyntaxAtom>)

@kotlin.jvm.JvmInline
value class Try(val syntax: Syntax)

@kotlin.jvm.JvmInline
value class Update(val fieldStack: Stack<SyntaxField>)

@kotlin.jvm.JvmInline
value class With(val syntax: Syntax)

sealed class LetRhs
data class BeLetRhs(val be: Be) : LetRhs()
data class DoLetRhs(val do_: Do) : LetRhs()
data class HaveLetRhs(val have: Have) : LetRhs()
data class ApplyLetRhs(val apply: Apply) : LetRhs()

sealed class Block
data class SyntaxBlock(val syntax: Syntax) : Block()
data class RecursingBlock(val recursing: Recursing) : Block()

sealed class IsRhs
data class SyntaxIsRhs(val syntax: Syntax) : IsRhs()
data class EqualIsRhs(val equal: Equal) : IsRhs()
data class MatchingIsRhs(val matching: Matching) : IsRhs()

val Get.nameSeq get() = nameStack.reverse.seq
val Syntax.lineSeq get() = lineStack.reverse.seq
val Set.atomSeq get() = atomStack.reverse.seq
val Select.caseSeq get() = caseStack.reverse.seq
val The.atomSeq get() = atomStack.reverse.seq
val Update.fieldSeq get() = fieldStack.reverse.seq

fun syntax(name: String) = syntax(name lineTo syntax())
fun syntax(vararg syntaxLines: SyntaxLine): Syntax = Syntax(stack(*syntaxLines))
fun Syntax.plus(line: SyntaxLine): Syntax = Syntax(lineStack.push(line))
fun select(vararg cases: Case): Select = Select(stack(*cases))

fun syntaxLine(name: String) = name lineTo syntax()
infix fun String.lineTo(syntax: Syntax) = line(atom(this fieldTo syntax))
infix fun String.fieldTo(syntax: Syntax) = SyntaxField(this, syntax)
infix fun String.caseTo(syntax: Syntax) = Case(this, syntax)

fun line(apply: Apply): SyntaxLine = ApplySyntaxLine(apply)
fun line(applying: Applying): SyntaxLine = ApplyingSyntaxLine(applying)
fun line(as_: As): SyntaxLine = AsSyntaxLine(as_)

//fun line(be: Be): SyntaxLine = BeSyntaxLine(be)
fun line(being: Being): SyntaxLine = BeingSyntaxLine(being)
fun line(end_: End): SyntaxLine = EndSyntaxLine(end_)
fun line(check: Check): SyntaxLine = CheckSyntaxLine(check)
fun line(comment: Comment): SyntaxLine = CommentSyntaxLine(comment)
fun line(combineWith: CombineWith): SyntaxLine = CombineWithSyntaxLine(combineWith)
fun line(combiningWith: CombiningWith): SyntaxLine = CombiningWithSyntaxLine(combiningWith)
fun line(debug: Debug): SyntaxLine = DebugSyntaxLine(debug)
fun line(do_: Do): SyntaxLine = DoSyntaxLine(do_)
fun line(doing: Doing): SyntaxLine = DoingSyntaxLine(doing)

//fun line(example: Example): SyntaxLine = ExampleSyntaxLine(example)
//fun line(fail: Fail): SyntaxLine = FailSyntaxLine(fail)
fun line(field: SyntaxField): SyntaxLine = line(atom(field))
fun line(get: Get): SyntaxLine = GetSyntaxLine(get)

//fun line(give: Give): SyntaxLine = GiveSyntaxLine(give)
fun line(help: Help): SyntaxLine = HelpSyntaxLine(help)
fun line(is_: Is): SyntaxLine = IsSyntaxLine(is_)
fun line(let: Let): SyntaxLine = LetSyntaxLine(let)
fun line(load: Load): SyntaxLine = LoadSyntaxLine(load)
fun line(repeat: Repeat): SyntaxLine = RepeatSyntaxLine(repeat)
fun syntaxLine(literal: Literal): SyntaxLine = line(syntaxAtom(literal))
fun line(atom: SyntaxAtom): SyntaxLine = AtomSyntaxLine(atom)
fun line(matching: Matching): SyntaxLine = MatchingSyntaxLine(matching)
fun line(select: Select): SyntaxLine = SelectSyntaxLine(select)
fun line(private: Private): SyntaxLine = PrivateSyntaxLine(private)
fun line(recurse: Recurse): SyntaxLine = RecurseSyntaxLine(recurse)
fun line(recursive: Recursive): SyntaxLine = RecursiveSyntaxLine(recursive)
fun line(quote: Quote): SyntaxLine = QuoteSyntaxLine(quote)
fun line(set: Set): SyntaxLine = SetSyntaxLine(set)

//fun line(take: Take): SyntaxLine = TakeSyntaxLine(take)
fun line(test: Test): SyntaxLine = TestSyntaxLine(test)
fun line(the: The): SyntaxLine = TheSyntaxLine(the)
fun line(try_: Try): SyntaxLine = TrySyntaxLine(try_)
fun line(update: Update): SyntaxLine = UpdateSyntaxLine(update)
fun line(use: Use): SyntaxLine = UseSyntaxLine(use)
//fun line(with: With): SyntaxLine = WithSyntaxLine(with)

fun letRhs(be: Be): LetRhs = BeLetRhs(be)
fun letRhs(do_: Do): LetRhs = DoLetRhs(do_)

fun apply(block: Block) = Apply(block)
fun applying(block: Block) = Applying(block)
fun as_(syntax: Syntax) = As(syntax)
fun be(syntax: Syntax) = Be(syntax)
fun being(syntax: Syntax) = Being(syntax)
fun end(syntax: Syntax) = End(syntax)
fun check(is_: Is) = Check(is_)
fun comment(script: Script) = Comment(script)
fun combineWith(block: Block) = CombineWith(block)
fun combiningWith(block: Block) = CombiningWith(block)
fun debug(script: Script) = Debug(script)
fun do_(block: Block) = Do(block)
fun doing(block: Block) = Doing(block)
fun equal(syntax: Syntax) = Equal(syntax)
fun example(syntax: Syntax) = Example(syntax)
fun fail(syntax: Syntax) = Fail(syntax)
fun get(vararg names: String) = Get(stack(*names))
fun have(syntax: Syntax) = Have(syntax)
fun help(script: Script) = Help(script)
fun give(syntax: Syntax) = Give(syntax)
fun is_(rhs: IsRhs) = Is(rhs, negated = false)
fun is_(rhs: IsRhs, negated: Boolean) = Is(rhs, negated)
fun matching(syntax: Syntax) = Matching(syntax)
fun not(syntax: Syntax) = Not(syntax)
fun let(syntax: Syntax, rhs: LetRhs) = Let(syntax, rhs)
fun let(syntax: Syntax, be: Be) = Let(syntax, BeLetRhs(be))
fun let(syntax: Syntax, do_: Do) = Let(syntax, DoLetRhs(do_))
fun let(syntax: Syntax, have: Have) = Let(syntax, HaveLetRhs(have))
fun let(syntax: Syntax, apply: Apply) = Let(syntax, ApplyLetRhs(apply))
fun repeat(syntax: Syntax) = Repeat(syntax)
fun private(syntax: Syntax) = Private(syntax)
fun recurse(syntax: Syntax) = Recurse(syntax)
fun recursing(syntax: Syntax) = Recursing(syntax)
fun recursive(syntax: Syntax) = Recursive(syntax)
fun quote(script: Script) = Quote(script)
fun set(vararg atoms: SyntaxAtom) = Set(stack(*atoms))
fun take(syntax: Syntax) = Take(syntax)
fun the(atomStack: Stack<SyntaxAtom>) = The(atomStack)
fun the(vararg atoms: SyntaxAtom) = The(stack(*atoms))
fun test(syntax: Syntax, is_: Is) = Test(syntax, is_)
fun try_(syntax: Syntax) = Try(syntax)
fun update(vararg fields: SyntaxField) = Update(stack(*fields))
fun with(syntax: Syntax) = With(syntax)
fun load(name: String, vararg names: String) = Load(stackLink(name, *names))
fun load(nameStackLink: StackLink<String>) = Load(nameStackLink)

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

val Script.loadOrNull: Load?
  get() =
    nameStackOrNull?.reverse?.linkOrNull?.let { nameStackLink ->
      Load(nameStackLink)
    }
