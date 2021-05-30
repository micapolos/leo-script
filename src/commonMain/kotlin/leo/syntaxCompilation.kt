package leo

import leo.natives.getName

val Script.syntax get() = syntaxCompilation.get

val Script.syntaxCompilation: Compilation<Syntax> get() =
	lineStack.map { opCompilation }.flat.map(::Syntax)

val ScriptLine.opCompilation: Compilation<Op> get() =
	when (this) {
		is FieldScriptLine -> field.opCompilation
		is LiteralScriptLine -> literal.opCompilation
	}

val ScriptLine.opFieldCompilation: Compilation<OpField> get() =
	fieldOrNull
		.notNullOrThrow { script(this, notName lineTo script("field")).value }
		.opFieldCompilation

val ScriptField.opCompilation: Compilation<Op> get() =
	when (string) {
		asName -> rhs.asCompilation.map(::op)
		beName -> rhs.beCompilation.map(::op)
		commentName -> rhs.commentCompilation.map(::op)
		doName -> rhs.doCompilation.map(::op)
		doingName -> rhs.doingCompilation.map(::op)
		failName -> rhs.failCompilation.map(::op)
		getName -> rhs.getCompilation.map(::op)
		matchingName -> rhs.matchingCompilation.map(::op)
		letName -> rhs.letCompilation.map(::op)
		setName -> rhs.setCompilation.map(::op)
		switchName -> rhs.switchCompilation.map(::op)
		tryName -> rhs.tryCompilation.map(::op)
		updateName -> rhs.updateCompilation.map(::op)
		useName -> rhs.useCompilation.map(::op)
		withName -> rhs.withCompilation.map(::op)
		else -> opFieldCompilation.map(::op)
	}

val ScriptField.opFieldCompilation: Compilation<OpField> get() =
	rhs.syntaxCompilation.map { rhsExpression ->
		OpField(string, ExpressionOpFieldRhs(rhsExpression))
	}

val Literal.opCompilation: Compilation<Op> get() =
	when (this) {
		is NumberLiteral -> FieldOp(OpField(numberName, NativeOpFieldRhs(native(number)))).compilation
		is StringLiteral -> FieldOp(OpField(textName, NativeOpFieldRhs(native(string)))).compilation
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
	lineStack.map { opFieldCompilation }.flat.map(::Set)

val Script.tryCompilation: Compilation<Try> get() =
	syntaxCompilation.map(::try_)

val Script.updateCompilation: Compilation<Update> get() =
	lineStack.map { opFieldCompilation }.flat.map(::Update)

val Script.useCompilation: Compilation<Use> get() =
	useOrNull.notNullOrThrow { value(useName fieldTo value) }.compilation

val Script.withCompilation: Compilation<With> get() =
	syntaxCompilation.map(::with)
