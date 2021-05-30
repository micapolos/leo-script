package leo

import leo.natives.getName

val Script.syntax get() = syntaxCompilation.get

val Script.syntaxCompilation: Compilation<Syntax> get() =
	lineStack.map { syntaxLineCompilation }.flat.map(::Syntax)

val ScriptLine.syntaxLineCompilation: Compilation<SyntaxLine> get() =
	when (this) {
		is FieldScriptLine -> field.syntaxLineCompilation
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
		doingName -> rhs.doingCompilation.map(::line)
		failName -> rhs.failCompilation.map(::line)
		getName -> rhs.getCompilation.map(::line)
		matchingName -> rhs.matchingCompilation.map(::line)
		letName -> rhs.letCompilation.map(::line)
		setName -> rhs.setCompilation.map(::line)
		switchName -> rhs.switchCompilation.map(::line)
		tryName -> rhs.tryCompilation.map(::line)
		updateName -> rhs.updateCompilation.map(::line)
		useName -> rhs.useCompilation.map(::line)
		withName -> rhs.withCompilation.map(::line)
		else -> syntaxFieldCompilation.map(::line)
	}

val ScriptField.syntaxFieldCompilation: Compilation<SyntaxField> get() =
	rhs.syntaxCompilation.map { rhsExpression ->
		SyntaxField(string, ExpressionSyntaxRhs(rhsExpression))
	}

val Literal.syntaxLineCompilation: Compilation<SyntaxLine> get() =
	when (this) {
		is NumberLiteral -> FieldSyntaxLine(SyntaxField(numberName, NativeSyntaxRhs(native(number)))).compilation
		is StringLiteral -> FieldSyntaxLine(SyntaxField(textName, NativeSyntaxRhs(native(string)))).compilation
	}

val Script.switchCompilation: Compilation<Switch> get() =
	lineStack.map { caseCompilation }.flat.map(::Switch)

val ScriptLine.caseCompilation: Compilation<Case> get() =
	when (this) {
		is FieldScriptLine -> field.caseCompilation
		is LiteralScriptLine -> value(notName fieldTo value("case")).throwError()
	}

val ScriptField.caseCompilation: Compilation<Case> get() =
	rhs.rhsOrNull(doingName).notNullOrThrow { value("case") }.doingCompilation.bind { doing ->
		Case(string, doing).compilation
	}

val Script.asCompilation: Compilation<As> get() =
	patternCompilation.map(::as_)

val Script.beCompilation: Compilation<Be> get() =
	syntaxCompilation.map(::be)

val Script.commentCompilation: Compilation<Comment> get() =
	comment(this).compilation

val Script.doCompilation: Compilation<Do> get() =
	syntaxCompilation.map(::do_)

val Script.doingCompilation: Compilation<Doing> get() =
	syntaxCompilation.map(::doing)

val Script.getCompilation: Compilation<Get> get() =
	lineStack
		.map { fieldOrNull?.onlyStringOrNull.notNullOrThrow { value(getName fieldTo value(field)) } }
		.let(::Get)
		.compilation

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

val Script.failCompilation: Compilation<Fail> get() =
	syntaxCompilation.map(::fail)

val Script.matchingCompilation: Compilation<Matching> get() =
	patternCompilation.map(::matching)

val Script.notCompilation: Compilation<Not> get() =
	syntaxCompilation.map(::not)

val Script.setCompilation: Compilation<Set> get() =
	lineStack.map { syntaxFieldCompilation }.flat.map(::Set)

val Script.tryCompilation: Compilation<Try> get() =
	syntaxCompilation.map(::try_)

val Script.updateCompilation: Compilation<Update> get() =
	lineStack.map { syntaxFieldCompilation }.flat.map(::Update)

val Script.useCompilation: Compilation<Use> get() =
	useOrNull.notNullOrThrow { value(useName fieldTo value) }.compilation

val Script.withCompilation: Compilation<With> get() =
	syntaxCompilation.map(::with)
