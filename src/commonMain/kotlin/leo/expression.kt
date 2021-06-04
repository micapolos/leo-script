package leo

// For now, expression is just a reversed syntax, so there's no need to reverse during runtime.
// TODO: Implement proper expression to use during runtime.
data class Expression(val reverseSyntax: Syntax)
val Syntax.expression: Expression get() = Expression(reverse)

val Syntax.reverse: Syntax get() = Syntax(lineStack.map { reverse }.reverse)
val SyntaxLine.reverse: SyntaxLine get() =
	when (this) {
		is AnySyntaxLine -> any.reverse.let(::line)
		is AsSyntaxLine -> as_.reverse.let(::line)
		is AtomSyntaxLine -> atom.reverse.let(::line)
		is BeSyntaxLine -> be.reverse.let(::line)
		is BindSyntaxLine -> bind.reverse.let(::line)
		is CheckSyntaxLine -> check.reverse.let(::line)
		is CommentSyntaxLine -> comment.reverse.let(::line)
		is DoSyntaxLine -> do_.reverse.let(::line)
		is DoingSyntaxLine -> doing.reverse.let(::line)
		is EndSyntaxLine -> end.reverse.let(::line)
		is ExampleSyntaxLine -> example.reverse.let(::line)
		is FailSyntaxLine -> fail.reverse.let(::line)
		is GetSyntaxLine -> get.reverse.let(::line)
		is GiveSyntaxLine -> give.reverse.let(::line)
		is IsSyntaxLine -> is_.reverse.let(::line)
		is LetSyntaxLine -> let.reverse.let(::line)
		is MatchingSyntaxLine -> matching.reverse.let(::line)
		is PrivateSyntaxLine -> private.reverse.let(::line)
		is QuoteSyntaxLine -> quote.reverse.let(::line)
		is RecurseSyntaxLine -> recurse.reverse.let(::line)
		is RepeatSyntaxLine -> repeat.reverse.let(::line)
		is SetSyntaxLine -> set.reverse.let(::line)
		is SwitchSyntaxLine -> switch.reverse.let(::line)
		is TakeSyntaxLine -> take.reverse.let(::line)
		is TestSyntaxLine -> test.reverse.let(::line)
		is TrySyntaxLine -> try_.reverse.let(::line)
		is UpdateSyntaxLine -> update.reverse.let(::line)
		is UseSyntaxLine -> use.reverse.let(::line)
		is WithSyntaxLine -> with.reverse.let(::line)
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
val Is.reverse get() = is_(rhs.reverse, negated)
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

val SyntaxAtom.reverse get() =
	when (this) {
		is FieldSyntaxAtom -> atom(field.reverse)
		is LiteralSyntaxAtom -> this
	}

val SyntaxField.reverse get() =
	name fieldTo rhsSyntax.reverse

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