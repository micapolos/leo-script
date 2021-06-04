package leo

// For now, expression is just a reversed syntax, so there's no need to reverse during runtime.
// TODO: Implement proper expression to use during runtime.
data class Expression(val reverseSyntax: Syntax)
val Syntax.expression: Expression get() = Expression(reverse)

val Syntax.reverse: Syntax get() = Syntax(lineStack.map { reverse }.reverse)
val SyntaxLine.reverse: SyntaxLine get() =
	when (this) {
		is AnySyntaxLine -> this
		is AsSyntaxLine -> TODO()
		is AtomSyntaxLine -> TODO()
		is BeSyntaxLine -> TODO()
		is BindSyntaxLine -> TODO()
		is CheckSyntaxLine -> TODO()
		is CommentSyntaxLine -> TODO()
		is DoSyntaxLine -> TODO()
		is DoingSyntaxLine -> TODO()
		is EndSyntaxLine -> TODO()
		is ExampleSyntaxLine -> TODO()
		is FailSyntaxLine -> TODO()
		is GetSyntaxLine -> TODO()
		is GiveSyntaxLine -> TODO()
		is IsSyntaxLine -> TODO()
		is LetSyntaxLine -> TODO()
		is MatchingSyntaxLine -> TODO()
		is PrivateSyntaxLine -> TODO()
		is QuoteSyntaxLine -> TODO()
		is RecurseSyntaxLine -> TODO()
		is RepeatSyntaxLine -> TODO()
		is SetSyntaxLine -> TODO()
		is SwitchSyntaxLine -> TODO()
		is TakeSyntaxLine -> TODO()
		is TestSyntaxLine -> TODO()
		is TrySyntaxLine -> TODO()
		is UpdateSyntaxLine -> TODO()
		is UseSyntaxLine -> TODO()
		is WithSyntaxLine -> TODO()
	}

val SyntaxAny.reverse get() = this
val As.reverse get() = as_(syntax.reverse)
val Be.reverse get() = be(syntax.reverse)
val Bind.reverse get() = bind(syntax.reverse)
val Check.reverse get() = check(is_.reverse)
val Comment.reverse get() = this
val Do.reverse get() = do_(block.reverse)
val Doing.reverse get() = doing(block.reverse)
val End.reverse get() = end(syntax.reverse)
val Equal.reverse get() = equal(syntax.reverse)
val Example.reverse get() = example(syntax.reverse)
val Fail.reverse get() = fail(syntax.reverse)
val Get.reverse get() = Get(nameStack.reverse)
val Give.reverse get() = give(syntax.reverse)
val Is.reverse get() = is_(rhs.reverse)
val Let.reverse get() = let(syntax.reverse, rhs.reverse)
val Matching.reverse get() = matching(syntax.reverse)
val Private.reverse get() = private(syntax.reverse)
val Quote.reverse get() = this
val Recurse.reverse get() = recurse(syntax.reverse)
val Recursing.reverse get() = recursing(syntax.reverse)
val Repeat.reverse get() = repeat(syntax.reverse)
val Set.reverse get() = Set(atomStack.reverse)
val Switch.reverse get() = Switch(caseStack.reverse)
val Take.reverse get() = take(syntax.reverse)
val Test.reverse get() = test(syntax.reverse, is_.reverse)
val Try.reverse get() = try_(syntax.reverse)
val Update.reverse get() = Update(fieldStack.reverse)
val Use.reverse get() = this
val With.reverse get() = with(syntax.reverse)

val Atom.reverse get() =
	when (this) {
		is LiteralAtom -> this
		is NameAtom -> this
	}

val IsRhs.reverse get() =
	when (this) {
		is EqualIsRhs -> isRhs(equal.reverse)
		is MatchingIsRhs -> isRhs(matching.reverse)
		is SyntaxIsRhs -> isRhs(syntax.reverse)
	}

val LetRhs.reverse get() =
	when (this) {
		is BeLetRhs -> letRhs(be.reverse)
		is DoLetRhs -> letRhs(do_.reverse)
	}

val Block.reverse get() =
	when (this) {
		is RecursingBlock -> block(recursing.reverse)
		is SyntaxBlock -> block(syntax.reverse)
	}