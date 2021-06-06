package leo

import leo.base.notNullIf
import leo.base.reverse
import leo.natives.getName

typealias Syntaxing<T> = Stateful<Unit, T>
val <T> T.syntaxing: Syntaxing<T> get() = stateful()
val <T> Syntaxing<T>.get get() = run(Unit).value

val Script.syntaxSyntaxing: Syntaxing<Syntax> get() =
	syntax().syntaxing.foldStateful(lineSeq.reverse) { plusCompilation(it) }

val ScriptLine.syntaxLineSyntaxing: Syntaxing<SyntaxLine> get() =
	when (this) {
		is FieldScriptLine -> field.syntaxLineSyntaxing
		is LiteralScriptLine -> line(syntaxAtom(literal)).syntaxing
	}

val ScriptLine.syntaxAtomSyntaxing: Syntaxing<SyntaxAtom> get() =
	when (this) {
		is FieldScriptLine -> field.syntaxAtomSyntaxing
		is LiteralScriptLine -> literal.syntaxLineSyntaxing
	}

val ScriptLine.syntaxFieldSyntaxing: Syntaxing<SyntaxField> get() =
	fieldOrNull
		.notNullOrThrow { script(this, notName lineTo script("field")).value }
		.syntaxFieldSyntaxing

fun Syntax.plusCompilation(scriptLine: ScriptLine): Syntaxing<Syntax> =
	scriptLine.syntaxLineSyntaxing.map { plus(it) }

val ScriptField.syntaxLineSyntaxing: Syntaxing<SyntaxLine> get() =
	when (string) {
		asName -> rhs.asSyntaxing.map(::line)
		beName -> rhs.beSyntaxing.map(::line)
		bindName -> rhs.bindSyntaxing.map(::line)
		endName -> rhs.endSyntaxing.map(::line)
		checkName -> rhs.checkSyntaxing.map(::line)
		commentName -> rhs.commentSyntaxing.map(::line)
		doName -> rhs.doSyntaxing.map(::line)
		doingName -> rhs.doingSyntaxingOrNull?.map(::line)
		exampleName -> rhs.exampleSyntaxing.map(::line)
		failName -> rhs.failSyntaxing.map(::line)
		getName -> rhs.getSyntaxing.map(::line)
		giveName -> rhs.giveSyntaxing.map(::line)
		isName -> rhs.isSyntaxing.map(::line)
		matchingName -> rhs.matchingSyntaxing.map(::line)
		letName -> rhs.letSyntaxing.map(::line)
		repeatName -> rhs.repeatSyntaxing.map(::line)
		privateName -> rhs.privateSyntaxing.map(::line)
		recurseName -> rhs.recurseSyntaxing.map(::line)
		quoteName -> rhs.quoteSyntaxing.map(::line)
		setName -> rhs.setSyntaxing.map(::line)
		switchName -> rhs.switchSyntaxing.map(::line)
		takeName -> rhs.takeSyntaxing.map(::line)
		testName -> rhs.testSyntaxing.map(::line)
		tryName -> rhs.trySyntaxing.map(::line)
		updateName -> rhs.updateSyntaxing.map(::line)
		useName -> rhs.useSyntaxing.map(::line)
		withName -> rhs.withSyntaxing.map(::line)
		else -> null
	} ?: syntaxAtomSyntaxing.map(::line)

val ScriptField.syntaxAtomSyntaxing: Syntaxing<SyntaxAtom> get() =
	syntaxFieldSyntaxing.map(::atom)

val ScriptField.syntaxFieldSyntaxing: Syntaxing<SyntaxField> get() =
	rhs.syntaxSyntaxing.map { rhsSyntax ->
		string fieldTo rhsSyntax
	}

val Literal.syntaxLineSyntaxing: Syntaxing<SyntaxAtom> get() =
	syntaxAtom(this).syntaxing

val Script.switchSyntaxing: Syntaxing<Switch> get() =
	lineStack.map { caseSyntaxing }.flat.map(::Switch)

val ScriptLine.caseSyntaxing: Syntaxing<Case> get() =
	when (this) {
		is FieldScriptLine -> field.caseSyntaxing
		is LiteralScriptLine -> value(notName fieldTo value("case")).throwError()
	}

val ScriptField.caseSyntaxing: Syntaxing<Case> get() =
	rhs.syntaxSyntaxing.map { syntax ->
		string caseTo syntax
	}

val Script.asSyntaxing: Syntaxing<As> get() =
	syntaxSyntaxing.map(::as_)

val Script.beSyntaxing: Syntaxing<Be> get() =
	syntaxSyntaxing.map(::be)

val Script.bindSyntaxing: Syntaxing<Bind> get() =
	syntaxSyntaxing.map(::bind)

val Script.endSyntaxing: Syntaxing<End> get() =
	syntaxSyntaxing.map(::end)

val Script.blockSyntaxing: Syntaxing<Block> get() =
	null
		?: recursingSyntaxingOrNull?.map(::block)
		?: syntaxSyntaxing.map(::block)

val Script.recursingSyntaxingOrNull: Syntaxing<Recursing>? get() =
	matchPrefix(recursingName) { rhs ->
		rhs.syntaxSyntaxing.map(::recursing)
	}

val Script.checkSyntaxing: Syntaxing<Check> get() =
	isSyntaxing.map(::check)

val Script.commentSyntaxing: Syntaxing<Comment> get() =
	comment(this).syntaxing

val Script.doSyntaxing: Syntaxing<Do> get() =
	blockSyntaxing.map(::do_)

val Script.doingSyntaxingOrNull: Syntaxing<Doing>? get() =
	notNullIf(!isEmpty && !equals(script(anyName))) {
		blockSyntaxing.map(::doing)
	}

val Script.exampleSyntaxing: Syntaxing<Example> get() =
	syntaxSyntaxing.map(::example)

val Script.getSyntaxing: Syntaxing<Get> get() =
	lineStack
		.map { fieldOrNull?.onlyStringOrNull.notNullOrThrow { value(getName fieldTo value(field)) } }
		.let(::Get)
		.syntaxing

val Script.giveSyntaxing: Syntaxing<Give> get() =
	syntaxSyntaxing.map(::give)

val Script.letSyntaxing: Syntaxing<Let> get() =
	matchInfix { lhs, name, rhs ->
		lhs.syntaxSyntaxing.bind { lhsValue ->
			when (name) {
				beName -> rhs.beSyntaxing.map { let(lhsValue, it) }
				doName -> rhs.doSyntaxing.map { let(lhsValue, it) }
				else -> value(syntaxName fieldTo value(letName fieldTo value)).throwError()
			}
		}
	}?:value(syntaxName fieldTo value(letName fieldTo value)).throwError()

val Script.repeatSyntaxing: Syntaxing<Repeat> get() =
	syntaxSyntaxing.map(::repeat)

val Script.privateSyntaxing: Syntaxing<Private> get() =
	syntaxSyntaxing.map(::private)

val Script.failSyntaxing: Syntaxing<Fail> get() =
	syntaxSyntaxing.map(::fail)

val Script.isSyntaxing: Syntaxing<Is> get() =
	matchPrefix(notName) { rhs ->
		rhs.isRhsSyntaxing.map(::is_).map { it.negate }
	} ?: isRhsSyntaxing.map(::is_)

val Script.isRhsSyntaxing: Syntaxing<IsRhs> get() =
	matchPrefix { name, rhs ->
		when (name) {
			matchingName -> rhs.matchingSyntaxing.map(::isRhs)
			equalName -> rhs.equalSyntaxing.map(::isRhs)
			else -> null
		}
	} ?: syntaxSyntaxing.map(::isRhs)

val Script.matchingSyntaxing: Syntaxing<Matching> get() =
	syntaxSyntaxing.map(::matching)

val Script.equalSyntaxing: Syntaxing<Equal> get() =
	syntaxSyntaxing.map(::equal)

val Script.recurseSyntaxing: Syntaxing<Recurse> get() =
	syntaxSyntaxing.map(::recurse)

val Script.quoteSyntaxing: Syntaxing<Quote> get() =
	quote(this).syntaxing

val Script.setSyntaxing: Syntaxing<Set> get() =
	lineStack.map { syntaxAtomSyntaxing }.flat.map(::Set)

val Script.takeSyntaxing: Syntaxing<Take> get() =
	syntaxSyntaxing.map(::take)

val Script.testSyntaxing: Syntaxing<Test> get() =
	matchInfix(isName) { lhs, rhs ->
		lhs.syntaxSyntaxing.bind { syntax ->
			rhs.isSyntaxing.map { is_ ->
				test(syntax, is_)
			}
		}
	}.notNullOrThrow { value(testName fieldTo value) }

val Script.trySyntaxing: Syntaxing<Try> get() =
	syntaxSyntaxing.map(::try_)

val Script.updateSyntaxing: Syntaxing<Update> get() =
	lineStack.map { syntaxFieldSyntaxing }.flat.map(::Update)

val Script.useSyntaxing: Syntaxing<Use> get() =
	useOrNull.notNullOrThrow { value(useName fieldTo value) }.syntaxing

val Script.withSyntaxing: Syntaxing<With> get() =
	syntaxSyntaxing.map(::with)