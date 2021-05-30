package leo

import leo.base.orNullIf

val Script.expression get() = expressionCompilation.get

val Script.expressionCompilation: Compilation<Expression> get() =
	when (this) {
		is UnitScript -> EmptyExpression.compilation
		is LinkScript -> link.expressionLinkCompilation.bind { link ->
			LinkExpression(link).compilation
		}
	}

val ScriptLink.expressionLinkCompilation: Compilation<ExpressionLink> get() =
	lhs.expressionCompilation.bind { expression ->
		line.opCompilation.bind { op ->
			ExpressionLink(expression, op).compilation
		}
	}

val ScriptLine.opCompilation: Compilation<Op> get() =
	when (this) {
		is FieldScriptLine -> field.opCompilation
		is LiteralScriptLine -> literal.opCompilation
	}

val ScriptField.opCompilation: Compilation<Op> get() =
	when (string) {
		asName -> rhs.asCompilation.map(::op)
		commentName -> rhs.commentCompilation.map(::op)
		doName -> rhs.doCompilation.map(::op)
		failName -> rhs.failCompilation.map(::op)
		switchName -> rhs.switchCompilation.map(::op)
		else -> rhs.expressionCompilation.map { rhsExpression ->
			FieldOp(OpField(string, ExpressionOpFieldRhs(rhsExpression)))
		}
	}

val Literal.opCompilation: Compilation<Op> get() =
	when (this) {
		is NumberLiteral -> FieldOp(OpField(numberName, NativeOpFieldRhs(native(number)))).compilation
		is StringLiteral -> FieldOp(OpField(textName, NativeOpFieldRhs(native(string)))).compilation
	}

val Script.switchCompilation: Compilation<Switch> get() =
	when (this) {
		is UnitScript -> EmptySwitch.compilation
		is LinkScript -> link.switchLinkCompilation.bind { link ->
			LinkSwitch(link).compilation
		}
	}

val ScriptLink.switchLinkCompilation: Compilation<SwitchLink> get() =
	lhs.switchCompilation.bind { switch ->
		line.caseCompilation.bind { case ->
			SwitchLink(switch, case).compilation
		}
	}

val ScriptLine.caseCompilation: Compilation<Case> get() =
	when (this) {
		is FieldScriptLine -> field.caseCompilation
		is LiteralScriptLine -> value(notName fieldTo value("case")).throwError()
	}

val ScriptField.caseCompilation: Compilation<Case> get() =
	rhs.expressionCompilation.bind { expression ->
		Case(string, expression).compilation
	}

val Script.asCompilation: Compilation<As> get() =
	patternCompilation.map(::as_)

val Script.commentCompilation: Compilation<Comment> get() =
	comment(this).compilation

val Script.doCompilation: Compilation<Do> get() =
	expressionCompilation.map(::do_)

val Script.patternCompilation: Compilation<Pattern> get() =
	pattern(this).compilation // TODO: Implement properly

val Script.failCompilation: Compilation<Fail> get() =
	if (isEmpty) fail.compilation
	else value(failName fieldTo value).throwError()
