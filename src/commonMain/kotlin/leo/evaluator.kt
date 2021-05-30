package leo

import leo.base.fold
import leo.base.orNullIf
import leo.base.reverse
import leo.base.negate
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

val String.dictionary: Dictionary
	get() =
		scriptOrThrow.dictionary

val Script.dictionary: Dictionary
	get() =
		preludeDictionary.context.evaluator().plusEvaluation(this).get.context.publicDictionary

fun Context.evaluatorEvaluation(script: Script): Evaluation<Evaluator> =
	evaluator().plusEvaluation(script)

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
		is BeSyntaxLine -> plusEvaluation(line.be)
		is CommentSyntaxLine -> plusEvaluation(line.comment)
		is DoSyntaxLine -> TODO()
		is DoingSyntaxLine -> TODO()
		is ExampleSyntaxLine -> TODO()
		is FailSyntaxLine -> TODO()
		is FieldSyntaxLine -> TODO()
		is GetSyntaxLine -> TODO()
		is IsSyntaxLine -> TODO()
		is LetSyntaxLine -> TODO()
		is LiteralSyntaxLine -> TODO()
		is MatchingSyntaxLine -> TODO()
		is PrivateSyntaxLine -> TODO()
		is RecurseSyntaxLine -> TODO()
		is RepeatSyntaxLine -> TODO()
		is QuoteSyntaxLine -> TODO()
		is SetSyntaxLine -> TODO()
		is SwitchSyntaxLine -> TODO()
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
		commentName -> evaluation
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
	dictionary.applyEvaluation(block(rhs), value).bind { setEvaluation(it) }

fun Evaluator.plusEvaluateEvaluation(rhs: Rhs): Evaluation<Evaluator> =
	dictionary.set(rhs.valueOrThrow).valueEvaluation(value.script).bind { evaluated ->
		setEvaluation(evaluated)
	}

fun Evaluator.plusExampleEvaluation(rhs: Rhs): Evaluation<Evaluator> =
	evaluation.also { rhs.valueOrThrow }

fun Evaluator.plusFailEvaluation(rhs: Script): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, rhs).bind { value ->
		evaluation.also { value.throwError() }
	}

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
	dictionary.valueEvaluation(test.syntax.plus(line(test.is_))).bind { result ->
		when (result) {
			true.isValue -> evaluation
			false.isValue ->
				dictionary.valueEvaluation(test.syntax).bind { lhs ->
					dictionary.valueEvaluation(test.is_.syntax).bind { rhs ->
						evaluation.also {
							value(testName fieldTo value("todo"))
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

fun Evaluator.plusDoingOrNullEvaluation(rhs: Script): Evaluation<Evaluator?> =
	rhs.orNullIf(rhs.isEmpty).evaluation.nullableBind {
		plusEvaluation(field(dictionary.function(body(rhs))))
	}

fun Evaluator.plusHashOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	if (rhs.valueOrNull?.isEmpty == true) setEvaluation(value.hashValue)
	else evaluation(null)

fun Evaluator.plusIsEqualEvaluation(rhs: Script, negate: Boolean): Evaluation<Evaluator?> =
	dictionary.valueEvaluation(rhs).bind {
		setEvaluation(value.equals(it).isValue(negate))
	}

fun Evaluator.plusQuoteEvaluation(rhs: Script): Evaluation<Evaluator> =
	setEvaluation(value.script.plus(rhs).value)

fun Evaluator.plusSetEvaluation(rhs: Script): Evaluation<Evaluator> =
	dictionary.valueRhsEvaluation(rhs).bind { rhsValue ->
		setEvaluation(value.setOrThrow(rhsValue))
	}

fun Evaluator.plusSwitchEvaluation(rhs: Script): Evaluation<Evaluator> =
	dictionary.switchEvaluation(value, rhs).bind {
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
	plusEvaluation(as_(pattern(rhs)))

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

fun Evaluator.plusIsMatchingEvaluation(rhs: Script, negate: Boolean): Evaluation<Evaluator> =
	setEvaluation(value.isMatching(pattern(rhs), negate))

fun Evaluator.plusPrivateEvaluation(rhs: Script): Evaluation<Evaluator> =
	context.private.evaluatorEvaluation(rhs).map { evaluator ->
		use(evaluator.context.publicDictionary)
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