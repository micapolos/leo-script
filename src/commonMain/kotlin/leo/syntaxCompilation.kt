package leo

import leo.base.notNullIf
import leo.natives.getName

val Script.syntax get() = syntaxCompilation.get

val Script.syntaxCompilation: Compilation<Syntax> get() =
	lineStack.map { syntaxLineCompilation }.flat.map(::Syntax)

val ScriptLine.syntaxLineCompilation: Compilation<SyntaxLine> get() =
	when (this) {
		is FieldScriptLine -> field.syntaxLineCompilation
		is LiteralScriptLine -> line(syntaxAtom(literal)).compilation
	}

val ScriptLine.syntaxAtomCompilation: Compilation<SyntaxAtom> get() =
	when (this) {
		is FieldScriptLine -> field.syntaxAtomCompilation
		is LiteralScriptLine -> literal.syntaxLineCompilation
	}

val ScriptLine.syntaxFieldCompilation: Compilation<SyntaxField> get() =
	fieldOrNull
		.notNullOrThrow { script(this, notName lineTo script("field")).value }
		.syntaxFieldCompilation

val ScriptField.syntaxLineCompilation: Compilation<SyntaxLine> get() =
	when (string) {
		asName -> rhs.asCompilation.map(::line)
		beName -> rhs.beCompilation.map(::line)
		commentName -> rhs.commentCompilation.map(::line)
		doName -> rhs.doCompilation.map(::line)
		doingName -> rhs.doingCompilationOrNull?.map(::line)
		exampleName -> rhs.exampleCompilation.map(::line)
		failName -> rhs.failCompilation.map(::line)
		getName -> rhs.getCompilation.map(::line)
		giveName -> rhs.giveCompilation.map(::line)
		isName -> rhs.isCompilation.map(::line)
		matchingName -> rhs.matchingCompilation.map(::line)
		letName -> rhs.letCompilation.map(::line)
		privateName -> rhs.privateCompilation.map(::line)
		recurseName -> rhs.recurseCompilation.map(::line)
		repeatName -> rhs.repeatCompilation.map(::line)
		quoteName -> rhs.quoteCompilation.map(::line)
		setName -> rhs.setCompilation.map(::line)
		switchName -> rhs.switchCompilation.map(::line)
		takeName -> rhs.takeCompilation.map(::line)
		testName -> rhs.testCompilation.map(::line)
		tryName -> rhs.tryCompilation.map(::line)
		updateName -> rhs.updateCompilation.map(::line)
		useName -> rhs.useCompilation.map(::line)
		withName -> rhs.withCompilation.map(::line)
		else -> null
	} ?: syntaxAtomCompilation.map(::line)

val ScriptField.syntaxAtomCompilation: Compilation<SyntaxAtom> get() =
	syntaxFieldCompilation.map(::atom)

val ScriptField.syntaxFieldCompilation: Compilation<SyntaxField> get() =
	rhs.syntaxCompilation.map { rhsSyntax ->
		string fieldTo rhsSyntax
	}

val Literal.syntaxLineCompilation: Compilation<SyntaxAtom> get() =
	syntaxAtom(this).compilation

val Script.switchCompilation: Compilation<Switch> get() =
	lineStack.map { caseCompilation }.flat.map(::Switch)

val ScriptLine.caseCompilation: Compilation<Case> get() =
	when (this) {
		is FieldScriptLine -> field.caseCompilation
		is LiteralScriptLine -> value(notName fieldTo value("case")).throwError()
	}

val ScriptField.caseCompilation: Compilation<Case> get() =
	rhs.rhsOrNull(doingName).notNullOrThrow { value("case") }.doingCompilationOrThrow.bind { doing ->
		Case(string, doing).compilation
	}

val Script.asCompilation: Compilation<As> get() =
	patternCompilation.map(::as_)

val Script.beCompilation: Compilation<Be> get() =
	syntaxCompilation.map(::be)

val Script.blockCompilation: Compilation<SyntaxBlock> get() =
	matchPrefix { name, rhs ->
		name
			.blockTypeOrNull
			?.let { blockType ->
				rhs.syntaxCompilation.map { syntax ->
					block(blockType, syntax)
				}
			}
	} ?: syntaxCompilation.map(::block)

val Script.commentCompilation: Compilation<Comment> get() =
	comment(this).compilation

val Script.doCompilation: Compilation<Do> get() =
	blockCompilation.map(::do_)

val Script.doingCompilationOrNull: Compilation<Doing>? get() =
	notNullIf(!isEmpty) {
		blockCompilation.map(::doing)
	}

val Script.doingCompilationOrThrow: Compilation<Doing> get() =
	doingCompilationOrNull.notNullOrThrow { value(doingName) }

val Script.exampleCompilation: Compilation<Example> get() =
	syntaxCompilation.map(::example)

val Script.getCompilation: Compilation<Get> get() =
	lineStack
		.map { fieldOrNull?.onlyStringOrNull.notNullOrThrow { value(getName fieldTo value(field)) } }
		.let(::Get)
		.compilation

val Script.giveCompilation: Compilation<Give> get() =
	syntaxCompilation.map(::give)

val Script.letCompilation: Compilation<Let> get() =
	matchInfix { lhs, name, rhs ->
		lhs.patternCompilation.bind { pattern ->
			when (name) {
				beName -> rhs.beCompilation.map { be ->
					let(pattern, be)
				}
				doName -> rhs.doCompilation.map { do_ ->
					let(pattern, do_)
				}
				else -> value().throwError()
			}
		}
	}?:value().throwError()

val Script.patternCompilation: Compilation<Pattern> get() =
	pattern(this).compilation // TODO: Implement properly

val Script.privateCompilation: Compilation<Private> get() =
	syntaxCompilation.map(::private)

val Script.failCompilation: Compilation<Fail> get() =
	syntaxCompilation.map(::fail)

val Script.isCompilation: Compilation<Is> get() =
	matchPrefix(notName) { rhs ->
		rhs.isRhsCompilation.map(::is_).map { it.negate }
	} ?: isRhsCompilation.map(::is_)

val Script.isRhsCompilation: Compilation<IsRhs> get() =
	matchPrefix { name, rhs ->
		when (name) {
			matchingName -> rhs.matchingCompilation.map(::isRhs)
			equalName -> rhs.equalCompilation.map(::isRhs)
			else -> null
		}
	} ?: syntaxCompilation.map(::isRhs)

val Script.matchingCompilation: Compilation<Matching> get() =
	patternCompilation.map(::matching)

val Script.equalCompilation: Compilation<Equal> get() =
	syntaxCompilation.map(::equal)

val Script.recurseCompilation: Compilation<Recurse> get() =
	syntaxCompilation.map(::recurse)

val Script.repeatCompilation: Compilation<Repeat> get() =
	syntaxCompilation.map(::repeat)

val Script.quoteCompilation: Compilation<Quote> get() =
	quote(this).compilation

val Script.setCompilation: Compilation<Set> get() =
	lineStack.map { syntaxAtomCompilation }.flat.map(::Set)

val Script.takeCompilation: Compilation<Take> get() =
	syntaxCompilation.map(::take)

val Script.testCompilation: Compilation<Test> get() =
	matchInfix(isName) { lhs, rhs ->
		lhs.syntaxCompilation.bind { syntax ->
			rhs.isCompilation.map { is_ ->
				test(syntax, is_)
			}
		}
	}.notNullOrThrow { value(testName fieldTo value) }

val Script.tryCompilation: Compilation<Try> get() =
	syntaxCompilation.map(::try_)

val Script.updateCompilation: Compilation<Update> get() =
	lineStack.map { syntaxFieldCompilation }.flat.map(::Update)

val Script.useCompilation: Compilation<Use> get() =
	useOrNull.notNullOrThrow { value(useName fieldTo value) }.compilation

val Script.withCompilation: Compilation<With> get() =
	syntaxCompilation.map(::with)
