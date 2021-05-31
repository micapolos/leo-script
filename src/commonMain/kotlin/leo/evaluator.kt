package leo

import leo.base.fold
import leo.base.negate
import leo.base.orNullIf
import leo.base.reverse
import leo.base.runIf
import leo.parser.scriptOrThrow
import leo.prelude.preludeDictionary

data class Evaluator(
	val context: Context,
	val value: Value
)

fun Context.evaluator(value: Value = value()) =
	Evaluator(this, value)

fun Evaluator.setEvaluation(value: Value): Evaluation<Evaluator> =
	context.evaluator(value).evaluation

fun Evaluator.set(context: Context): Evaluator =
	context.evaluator(value)

fun Dictionary.valueEvaluation(script: Script): Evaluation<Value> =
	context.evaluatorEvaluation(script).map { it.value }

fun Dictionary.valueEvaluation(expression: Expression): Evaluation<Value> =
	valueEvaluation(expression.script)

fun Dictionary.valueEvaluation(value: Value, script: Script): Evaluation<Value> =
	context.evaluator(value).plusEvaluation(script).map { it.value }

fun Dictionary.valueEvaluation(value: Value, syntax: Syntax): Evaluation<Value> =
	context.evaluator(value).plusEvaluation(syntax).map { it.value }

fun Dictionary.valueRhsEvaluation(script: Script): Evaluation<Value> =
	value().evaluation.fold(script.lineSeq.reverse) { line ->
		bind { value ->
			fieldEvaluation(line).bind { field ->
				value.plus(field).evaluation
			}
		}
	}

fun Dictionary.fieldEvaluation(scriptLine: ScriptLine): Evaluation<Field> =
	when (scriptLine) {
		is FieldScriptLine -> fieldEvaluation(scriptLine.field)
		is LiteralScriptLine -> field(scriptLine.literal).evaluation
	}

fun Dictionary.fieldEvaluation(scriptField: ScriptField): Evaluation<Field> =
	valueEvaluation(scriptField.rhs).map {
		scriptField.string fieldTo it
	}

fun Dictionary.fieldEvaluation(field: SyntaxField): Evaluation<Field> =
	valueEvaluation(field.rhsSyntax).map { rhs ->
		field.name fieldTo rhs
	}

fun Dictionary.fieldEvaluation(atom: SyntaxAtom): Evaluation<Field> =
	when (atom) {
		is FieldSyntaxAtom -> fieldEvaluation(atom.field)
		is LiteralSyntaxAtom -> field(atom.literal).evaluation
	}

val String.dictionary: Dictionary
	get() =
		scriptOrThrow.dictionary

val Script.dictionary: Dictionary
	get() =
		preludeDictionary.context.evaluator().plusEvaluation(this).get.context.publicDictionary

fun Context.evaluatorEvaluation(script: Script): Evaluation<Evaluator> =
	evaluator().plusEvaluation(script)

fun Context.evaluatorEvaluation(syntax: Syntax): Evaluation<Evaluator> =
	evaluator().plusEvaluation(syntax)

fun Evaluator.plusEvaluation(script: Script): Evaluation<Evaluator> =
	evaluation.fold(script.lineSeq.reverse) { line ->
		bind {
			it.plusEvaluation(line)
		}
	}

fun Dictionary.valueEvaluation(syntax: Syntax): Evaluation<Value> =
	context.evaluator(value()).plusEvaluation(syntax).map { it.value }

fun Evaluator.plusEvaluation(syntax: Syntax): Evaluation<Evaluator> =
	evaluation.foldStateful(syntax.lineStack.seq.reverse) { plusEvaluation(it) }

fun Evaluator.plusEvaluation(line: SyntaxLine): Evaluation<Evaluator> =
	when (line) {
		is AsSyntaxLine -> plusEvaluation(line.as_)
		is AtomSyntaxLine -> plusEvaluation(line.atom)
		is BeSyntaxLine -> plusEvaluation(line.be)
		is CommentSyntaxLine -> plusEvaluation(line.comment)
		is DoSyntaxLine -> plusEvaluation(line.do_)
		is DoingSyntaxLine -> plusEvaluation(line.doing)
		is ExampleSyntaxLine -> plusEvaluation(line.example)
		is FailSyntaxLine -> plusEvaluation(line.fail)
		is GetSyntaxLine -> plusEvaluation(line.get)
		is GiveSyntaxLine -> plusEvaluation(line.give)
		is IsSyntaxLine -> plusEvaluation(line.is_)
		is LetSyntaxLine -> plusEvaluation(line.let)
		is MatchingSyntaxLine -> plusEvaluation(line.matching)
		is PrivateSyntaxLine -> plusEvaluation(line.private)
		is RecurseSyntaxLine -> plusEvaluation(line.recurse)
		is RepeatSyntaxLine -> plusEvaluation(line.repeat)
		is QuoteSyntaxLine -> plusEvaluation(line.quote)
		is SetSyntaxLine -> plusEvaluation(line.set)
		is SwitchSyntaxLine -> plusEvaluation(line.switch)
		is TakeSyntaxLine -> plusEvaluation(line.take)
		is TestSyntaxLine -> plusEvaluation(line.test)
		is TrySyntaxLine -> plusEvaluation(line.try_)
		is UpdateSyntaxLine -> plusEvaluation(line.update)
		is UseSyntaxLine -> plusEvaluation(line.use)
		is WithSyntaxLine -> plusEvaluation(line.with)
	}

fun Evaluator.plusEvaluation(scriptLine: ScriptLine): Evaluation<Evaluator> =
	when (scriptLine) {
		is FieldScriptLine -> plusEvaluation(scriptLine.field)
		is LiteralScriptLine -> plusEvaluation(scriptLine.literal)
	}

fun Evaluator.plusEvaluation(atom: SyntaxAtom): Evaluation<Evaluator> =
	when (atom) {
		is FieldSyntaxAtom -> plusEvaluation(atom.field)
		is LiteralSyntaxAtom -> plusEvaluation(atom.literal)
	}

fun Evaluator.plusEvaluation(scriptField: ScriptField): Evaluation<Evaluator> =
	plusDefinitionsOrNullLEvaluation(scriptField).or {
		plusStaticOrNullEvaluation(scriptField).or {
			plusDynamicEvaluation(scriptField)
		}
	}

fun Evaluator.plusDefinitionsOrNullLEvaluation(scriptField: ScriptField): Evaluation<Evaluator?> =
	dictionary.definitionSeqOrNullEvaluation(scriptField).nullableMap { definitionSeq ->
		set(context.fold(definitionSeq.reverse) { plus(it) })
	}

fun Evaluator.plusStaticOrNullEvaluation(scriptField: ScriptField): Evaluation<Evaluator?> =
	when (scriptField.string) {
		asName -> plusAsEvaluation(scriptField.rhs)
		commentName -> plusEvaluation(comment(scriptField.rhs))
		doName -> plusDoEvaluation(scriptField.rhs)
		doingName -> plusDoingOrNullEvaluation(scriptField.rhs)
		failName -> plusFailEvaluation(scriptField.rhs)
		isName -> plusIsOrNullEvaluation(scriptField.rhs)
		privateName -> plusPrivateEvaluation(scriptField.rhs)
		quoteName -> plusQuoteEvaluation(scriptField.rhs)
		setName -> plusSetEvaluation(scriptField.rhs)
		switchName -> plusSwitchEvaluation(scriptField.rhs)
		testName -> plusTestEvaluation(scriptField.rhs)
		traceName -> plusTraceOrNullEvaluation(scriptField.rhs)
		tryName -> plusTryEvaluation(scriptField.rhs)
		updateName -> plusUpdateEvaluation(scriptField.rhs)
		useName -> plusUseEvaluation(scriptField.rhs)
		withName -> plusWithEvaluation(scriptField.rhs)
		else -> evaluation(null)
	}

fun Evaluator.plusDynamicOrNullEvaluation(field: Field): Evaluation<Evaluator?> =
	when (field.name) {
		giveName -> plusApplyEvaluation(field.rhs)
		beName -> plusBeEvaluation(field.rhs)
		evaluateName -> plusEvaluateEvaluation(field.rhs)
		exampleName -> plusExampleEvaluation(field.rhs)
		hashName -> plusHashOrNullEvaluation(field.rhs)
		takeName -> plusTakeEvaluation(field.rhs)
		textName -> plusTextOrNullEvaluation(field.rhs)
		valueName -> plusValueOrNullEvaluation(field.rhs)
		else -> evaluation(null)
	}

fun Evaluator.plusApplyEvaluation(rhs: Rhs): Evaluation<Evaluator> =
	value.functionOrThrow.evaluation.bind { function ->
		function.applyEvaluation(rhs.valueOrThrow).bind { output ->
			setEvaluation(output)
		}
	}

fun Evaluator.plusTakeEvaluation(rhs: Rhs): Evaluation<Evaluator> =
	rhs.valueOrThrow.functionOrThrow.evaluation.bind { function ->
		function.applyEvaluation(value).bind { output ->
			setEvaluation(output)
		}
	}

fun Evaluator.plusEvaluation(take: Take): Evaluation<Evaluator> =
	dictionary.valueEvaluation(take.syntax).bind { taken ->
		taken.functionOrThrow.evaluation.bind { function ->
			function.applyEvaluation(value).bind { output ->
				setEvaluation(output)
			}
		}
	}

fun Evaluator.plusTextOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	rhs.valueOrNull?.resolveEmptyOrNull {
		value.resolvePrefixOrNull(valueName) {
			value(field(literal(it.string)))
		}
	}
		.evaluation
		.nullableBind { setEvaluation(it) }

fun Evaluator.plusBeEvaluation(rhs: Rhs): Evaluation<Evaluator> =
	setEvaluation(rhs.valueOrThrow)

fun Evaluator.plusEvaluation(be: Be): Evaluation<Evaluator> =
	dictionary.valueEvaluation(be.syntax).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(comment: Comment): Evaluation<Evaluator> =
	evaluation

fun Evaluator.plusDoEvaluation(rhs: Script): Evaluation<Evaluator> =
	plusEvaluation(rhs.doCompilation.get)

fun Evaluator.plusEvaluation(do_: Do): Evaluation<Evaluator> =
	dictionary.applyEvaluation(do_.block.block, value).bind { setEvaluation(it) }

fun Evaluator.plusEvaluateEvaluation(rhs: Rhs): Evaluation<Evaluator> =
	dictionary.set(rhs.valueOrThrow).valueEvaluation(value.script).bind { evaluated ->
		setEvaluation(evaluated)
	}

fun Evaluator.plusExampleEvaluation(rhs: Rhs): Evaluation<Evaluator> =
	evaluation.also { rhs.valueOrThrow }

fun Evaluator.plusEvaluation(example: Example): Evaluation<Evaluator> =
	dictionary.valueEvaluation(example.syntax).bind { evaluation }

fun Evaluator.plusFailEvaluation(rhs: Script): Evaluation<Evaluator> =
	plusEvaluation(rhs.failCompilation.get)

fun Evaluator.plusEvaluation(fail: Fail): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, fail.syntax).bind { value ->
		evaluation.also { value.throwError() }
	}

fun Evaluator.plusEvaluation(matching: Matching): Evaluation<Evaluator> =
	plusEvaluation(matchingName fieldTo rhs(matching.pattern))

fun Evaluator.plusTestEvaluation(test: Script): Evaluation<Evaluator> =
	test.matchInfix(isName) { lhs, rhs ->
		dictionary.valueEvaluation(test).bind { result ->
			when (result) {
				true.isValue -> evaluation
				false.isValue ->
					dictionary.valueEvaluation(lhs).bind { lhs ->
						dictionary.valueEvaluation(rhs).bind { rhs ->
							evaluation.also {
								value(testName fieldTo test.value)
									.plus(
										causeName fieldTo
											lhs.plus(isName fieldTo value(notName fieldTo rhs))
									).throwError()
							}
						}
					}
				else -> evaluation.also {
					value(
						testName fieldTo result.plus(
							isName fieldTo value(
								notName fieldTo value(
									matchingName fieldTo value(
										isName fieldTo value(anyName)
									)
								)
							)
						)
					).throwError()
				}
			}
		}
	}.notNullOrThrow {
		value(syntaxName fieldTo value(testName fieldTo test.value))
	}

fun Evaluator.plusEvaluation(test: Test): Evaluation<Evaluator> =
	TODO()
//	dictionary.valueEvaluation(test.syntax.plus(line(test.is_))).bind { result ->
//		when (result) {
//			true.isValue -> evaluation
//			false.isValue ->
//				dictionary.valueEvaluation(test.syntax).bind { lhs ->
//					dictionary.valueEvaluation(test.is_.rhs.syntax).bind { rhs ->
//						evaluation.also {
//							value(testName fieldTo test.script.value)
//								.plus(
//									causeName fieldTo
//											lhs.plus(isName fieldTo value(notName fieldTo rhs))
//								).throwError()
//						}
//					}
//				}
//			else -> evaluation.also {
//				value(
//					testName fieldTo result.plus(
//						isName fieldTo value(
//							notName fieldTo value(
//								matchingName fieldTo value(
//									isName fieldTo value(anyName)
//								)
//							)
//						)
//					)
//				).throwError()
//			}
//		}
//	}

fun Evaluator.plusEvaluation(get: Get): Evaluation<Evaluator> =
	setEvaluation(value.apply(get))

fun Evaluator.plusEvaluation(give: Give): Evaluation<Evaluator> =
	value.functionOrThrow.evaluation.bind { function ->
		dictionary.valueEvaluation(give.syntax).bind { given ->
			function.applyEvaluation(given).bind { output ->
				setEvaluation(output)
			}
		}
	}


fun Evaluator.plusEvaluation(field: SyntaxField): Evaluation<Evaluator> =
	dictionary.fieldEvaluation(field).bind { field ->
		plusEvaluation(field)
	}

fun Evaluator.plusEvaluation(let: Let): Evaluation<Evaluator> =
	dictionary.bindingEvaluation(let.rhs).bind { binding ->
		set(context.plus(definition(let.pattern, binding))).evaluation
	}

fun Evaluator.plusDoingOrNullEvaluation(rhs: Script): Evaluation<Evaluator?> =
	rhs.doingCompilationOrNull.evaluation.nullableBind { plusEvaluation(it.get) }

fun Evaluator.plusEvaluation(doing: Doing): Evaluation<Evaluator> =
	plusEvaluation(field(dictionary.function(body(doing.block.block))))

fun Evaluator.plusHashOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	if (rhs.valueOrNull?.isEmpty == true) setEvaluation(value.hashValue)
	else evaluation(null)

fun Evaluator.plusIsEqualEvaluation(rhs: Script, negate: Boolean): Evaluation<Evaluator?> =
	dictionary.valueEvaluation(rhs).bind {
		setEvaluation(value.equals(it).isValue(negate))
	}

fun Evaluator.plusQuoteEvaluation(rhs: Script): Evaluation<Evaluator> =
	setEvaluation(value.script.plus(rhs).value)

fun Evaluator.plusEvaluation(quote: Quote): Evaluation<Evaluator> =
	setEvaluation(value.plus(quote.script.value))

fun Evaluator.plusSetEvaluation(rhs: Script): Evaluation<Evaluator> =
	dictionary.valueRhsEvaluation(rhs).bind { rhsValue ->
		setEvaluation(value.setOrThrow(rhsValue))
	}

fun Evaluator.plusSwitchEvaluation(rhs: Script): Evaluation<Evaluator> =
	dictionary.switchEvaluation(value, rhs).bind {
		setEvaluation(it)
	}

fun Evaluator.plusEvaluation(switch: Switch): Evaluation<Evaluator> =
	dictionary.evaluation(value, switch).bind {
		setEvaluation(it)
	}

fun Evaluator.plusTraceOrNullEvaluation(rhs: Script): Evaluation<Evaluator?> =
	rhs
		.matchEmpty { traceValueEvaluation.bind { setEvaluation(it) } }
		?: evaluation(null)

fun Evaluator.plusTryEvaluation(rhs: Script): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, rhs)
		.bind { value -> setEvaluation(value(tryName fieldTo value(successName fieldTo value))) }
		.catch { throwable -> setEvaluation(value(tryName fieldTo throwable.value)) }

fun Evaluator.plusEvaluation(try_: Try): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, try_.syntax)
		.bind { value -> setEvaluation(value(tryName fieldTo value(successName fieldTo value))) }
		.catch { throwable -> setEvaluation(value(tryName fieldTo throwable.value)) }

fun Evaluator.plusUpdateEvaluation(rhs: Script): Evaluation<Evaluator> =
	dictionary.updateEvaluation(value, rhs).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(update: Update): Evaluation<Evaluator> =
	dictionary.evaluation(value, update).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(set: Set): Evaluation<Evaluator> =
	dictionary.evaluation(value, set).bind { setEvaluation(it) }

fun Evaluator.plusDynamicEvaluation(scriptField: ScriptField): Evaluation<Evaluator> =
	dictionary.fieldEvaluation(scriptField).bind { field ->
		plusDynamicOrNullEvaluation(field).or {
			plusEvaluation(field)
		}
	}

fun Evaluator.plusEvaluation(literal: Literal): Evaluation<Evaluator> =
	plusEvaluation(field(literal))

fun Evaluator.plusEvaluation(field: Field): Evaluation<Evaluator> =
	dictionary.resolveEvaluation(value.plus(field)).bind {
		setEvaluation(it)
	}

fun Evaluator.plusAsEvaluation(rhs: Script): Evaluation<Evaluator> =
	plusEvaluation(rhs.asCompilation.get)

fun Evaluator.plusEvaluation(as_: As): Evaluation<Evaluator> =
	setEvaluation(value.as_(as_.pattern))

fun Evaluator.plusIsOrNullEvaluation(rhs: Script, negate: Boolean = false): Evaluation<Evaluator?> =
	rhs.onlyLineOrNull?.fieldOrNull.evaluation.nullableBind { field ->
		when (field.string) {
			equalName -> plusIsEqualEvaluation(field.rhs, negate)
			matchingName -> plusIsMatchingEvaluation(field.rhs, negate)
			// TODO: It's not working with "is not less than" and others
			notName -> plusIsOrNullEvaluation(field.rhs, negate.negate)
			else -> evaluation(null)
		}
	}

fun Evaluator.plusEvaluation(is_: Is): Evaluation<Evaluator> =
	booleanEvaluation(is_.rhs).bind { boolean ->
		setEvaluation(boolean.runIf(is_.negated) { negate }.isValue)
	}

fun Evaluator.booleanEvaluation(rhs: IsRhs): Evaluation<Boolean> =
	when (rhs) {
		is EqualIsRhs -> booleanEvaluation(rhs.equal)
		is MatchingIsRhs -> booleanEvaluation(rhs.matching)
		is SyntaxIsRhs -> booleanIsEvaluation(rhs.syntax)
	}

fun Evaluator.booleanEvaluation(equal: Equal): Evaluation<Boolean> =
	dictionary.valueEvaluation(equal.syntax).map { value == it }

fun Evaluator.booleanEvaluation(matching: Matching): Evaluation<Boolean> =
	value.isMatching(matching.pattern).evaluation

fun Evaluator.booleanIsEvaluation(syntax: Syntax): Evaluation<Boolean> =
	dictionary.valueEvaluation(syntax).bind { rhsValue ->
		dictionary.resolveEvaluation(value.plus(isName fieldTo rhsValue)).map { isValue ->
			isValue.isBoolean
		}
	}

fun Evaluator.plusIsMatchingEvaluation(rhs: Script, negate: Boolean): Evaluation<Evaluator> =
	setEvaluation(value.isMatching(pattern(rhs), negate))

fun Evaluator.plusPrivateEvaluation(rhs: Script): Evaluation<Evaluator> =
	context.private.evaluatorEvaluation(rhs).map { evaluator ->
		use(evaluator.context.publicDictionary)
	}

fun Evaluator.plusEvaluation(private: Private): Evaluation<Evaluator> =
	context.private.evaluatorEvaluation(private.syntax).map { evaluator ->
		use(evaluator.context.publicDictionary)
	}

fun Evaluator.plusEvaluation(recurse: Recurse): Evaluation<Evaluator> =
	dictionary.valueEvaluation(recurse.syntax).bind { repeatValue ->
		plusEvaluation(recurseName fieldTo repeatValue)
	}

fun Evaluator.plusEvaluation(repeat: Repeat): Evaluation<Evaluator> =
	dictionary.valueEvaluation(repeat.syntax).bind { repeatValue ->
		plusEvaluation(repeatName fieldTo repeatValue)
	}

fun Evaluator.plusUseEvaluation(rhs: Script): Evaluation<Evaluator> =
	rhs.useOrNull.notNullOrThrow { value.plus(useName fieldTo rhs.value) }.evaluation.bind {
		plusEvaluation(it)
	}

fun Evaluator.plusValueOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	rhs.valueOrNull?.resolveEmptyOrNull {
		value.textOrNull?.let {
			value(valueName fieldTo it.scriptOrThrow.value)
		}
	}
		.evaluation
		.nullableBind { setEvaluation(it) }


fun Evaluator.plusWithEvaluation(rhs: Script): Evaluation<Evaluator> =
	dictionary.valueEvaluation(rhs).bind { rhsValue ->
		setEvaluation(value + rhsValue)
	}

fun Evaluator.plusEvaluation(with: With): Evaluation<Evaluator> =
	dictionary.valueEvaluation(with.syntax).bind { rhsValue ->
		setEvaluation(value + rhsValue)
	}

val Evaluator.dictionary
	get() =
		context.privateDictionary

fun Evaluator.plusEvaluation(use: Use): Evaluation<Evaluator> =
	Evaluation { it.libraryEffect(use) }.map { use(it) }

fun Evaluator.use(dictionary: Dictionary): Evaluator =
	set(context.plusPrivate(dictionary))