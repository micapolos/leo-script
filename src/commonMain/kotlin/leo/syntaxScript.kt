package leo

import leo.base.runIf

val Syntax.script: Script get() =
	script(lineStack.map { scriptLine })

val SyntaxLine.scriptLine: ScriptLine get() =
	when (this) {
		is AtomSyntaxLine -> atom.scriptLine
		is AsSyntaxLine -> asName lineTo as_.script
		is BeSyntaxLine -> beName lineTo be.script
		is CommentSyntaxLine -> commentName lineTo comment.script
		is DoSyntaxLine -> doName lineTo do_.script
		is DoingSyntaxLine -> doingName lineTo doing.script
		is ExampleSyntaxLine -> exampleName lineTo example.script
		is FailSyntaxLine -> failName lineTo fail.script
		is GetSyntaxLine -> getName lineTo get.script
		is IsSyntaxLine -> isName lineTo is_.script
		is LetSyntaxLine -> letName lineTo let.script
		is MatchingSyntaxLine -> matchingName lineTo matching.script
		is PrivateSyntaxLine -> privateName lineTo private.script
		is QuoteSyntaxLine -> quoteName lineTo quote.script
		is RecurseSyntaxLine -> recurseName lineTo recurse.script
		is RepeatSyntaxLine -> repeatName lineTo repeat.script
		is SetSyntaxLine -> setName lineTo set.script
		is SwitchSyntaxLine -> switchName lineTo switch.script
		is TestSyntaxLine -> testName lineTo test.script
		is TrySyntaxLine -> tryName lineTo try_.script
		is UpdateSyntaxLine -> updateName lineTo update.script
		is UseSyntaxLine -> useName lineTo use.script
		is WithSyntaxLine -> withName lineTo with.script
	}

val As.script get() = pattern.script
val Be.script get() = syntax.script
val Do.script get() = block.script
val Doing.script get() = block.script
val Equal.script get() = syntax.script
val Example.script get() = syntax.script
val Fail.script get() = syntax.script
val Get.script get() = script().fold(nameStack) { script(it lineTo this) }
val Not.script get() = syntax.script
val Let.script get() = pattern.script.plus(rhs.scriptLine)
val Matching.script get() = pattern.script
val Private.script get() = syntax.script
val Recurse.script get() = syntax.script
val Repeat.script get() = syntax.script
val Set.script get() = script(atomStack.map { scriptLine })
val Switch.script get() = script(caseStack.map { scriptLine })
val Case.scriptLine get() = name lineTo script(doingName lineTo doing.script)
val Test.script get() = syntax.script.plus(isName lineTo is_.script)
val Try.script get() = syntax.script
val Update.script get() = script(fieldStack.map { scriptLine })
val Use.script get() = script().fold(stack(nameStackLink)) { script(it lineTo this) }
val With.script get() = syntax.script

val SyntaxField.scriptLine get() = name lineTo rhsSyntax.script

val SyntaxBlock.script get() =
	typeOrNull?.scriptName
		?.let { name -> script(name lineTo syntax.script) }
		?: syntax.script

val LetRhs.scriptLine get() =
	when (this) {
		is BeLetRhs -> beName lineTo be.script
		is DoLetRhs -> doName lineTo do_.script
	}

val Is.script get() =
	rhs.script.runIf(negated) { script(notName lineTo this) }

val IsRhs.script get() =
	when (this) {
		is EqualIsRhs -> script(equalName lineTo equal.script)
		is MatchingIsRhs -> script(matchingName lineTo matching.script)
		is SyntaxIsRhs -> syntax.script
	}

val SyntaxAtom.scriptLine get() =
	when (this) {
		is FieldSyntaxAtom -> field.scriptLine
		is LiteralSyntaxAtom -> literal.line
	}