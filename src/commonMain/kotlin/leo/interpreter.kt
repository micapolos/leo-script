package leo

import leo.base.fold
import leo.base.orNullIf
import leo.base.reverse
import leo.base.negate
import leo.parser.scriptOrThrow
import leo.prelude.preludeDictionary

data class Interpreter(
	val context: Context,
	val value: Value
)

fun Context.interpreter(value: Value = value()) =
	Interpreter(this, value)

fun Interpreter.setEvaluation(value: Value): Evaluation<Interpreter> =
	context.interpreter(value).evaluation

fun Interpreter.set(context: Context): Interpreter =
	context.interpreter(value)

fun Dictionary.valueEvaluation(script: Script): Evaluation<Value> =
	context.interpreterEvaluation(script).map { it.value }

fun Dictionary.valueEvaluation(expression: Expression): Evaluation<Value> =
	valueEvaluation(expression.script)

fun Dictionary.valueEvaluation(value: Value, script: Script): Evaluation<Value> =
	context.interpreter(value).plusEvaluation(script).map { it.value }

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
		preludeDictionary.context.interpreter().plusEvaluation(this).get.context.publicDictionary

fun Context.interpreterEvaluation(script: Script): Evaluation<Interpreter> =
	interpreter().plusEvaluation(script)

fun Interpreter.plusEvaluation(script: Script): Evaluation<Interpreter> =
	evaluation.fold(script.lineSeq.reverse) { line ->
		bind {
			it.plusEvaluation(line)
		}
	}

fun Dictionary.valueEvaluation(syntax: Syntax): Evaluation<Value> =
	context.interpreter(value()).plusEvaluation(syntax).map { it.value }

fun Interpreter.plusEvaluation(syntax: Syntax): Evaluation<Interpreter> =
	evaluation.foldStateful(syntax.lineStack.seq.reverse) { plusEvaluation(it) }

fun Interpreter.plusEvaluation(line: SyntaxLine): Evaluation<Interpreter> =
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
		is QuoteSyntaxLine -> TODO()
		is SetSyntaxLine -> TODO()
		is SwitchSyntaxLine -> TODO()
		is TestSyntaxLine -> TODO()
		is TrySyntaxLine -> TODO()
		is UpdateSyntaxLine -> TODO()
		is UseSyntaxLine -> TODO()
		is WithSyntaxLine -> TODO()
	}

fun Interpreter.plusEvaluation(scriptLine: ScriptLine): Evaluation<Interpreter> =
	when (scriptLine) {
		is FieldScriptLine -> plusEvaluation(scriptLine.field)
		is LiteralScriptLine -> plusEvaluation(scriptLine.literal)
	}

fun Interpreter.plusEvaluation(scriptField: ScriptField): Evaluation<Interpreter> =
	plusDefinitionsOrNullLEvaluation(scriptField).or {
		plusStaticOrNullEvaluation(scriptField).or {
			plusDynamicEvaluation(scriptField)
		}
	}

fun Interpreter.plusDefinitionsOrNullLEvaluation(scriptField: ScriptField): Evaluation<Interpreter?> =
	dictionary.definitionSeqOrNullEvaluation(scriptField).nullableMap { definitionSeq ->
		set(context.fold(definitionSeq.reverse) { plus(it) })
	}

fun Interpreter.plusStaticOrNullEvaluation(scriptField: ScriptField): Evaluation<Interpreter?> =
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

fun Interpreter.plusDynamicOrNullEvaluation(field: Field): Evaluation<Interpreter?> =
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

fun Interpreter.plusApplyEvaluation(rhs: Rhs): Evaluation<Interpreter> =
	value.functionOrThrow.evaluation.bind { function ->
		function.applyEvaluation(rhs.valueOrThrow).bind { output ->
			setEvaluation(output)
		}
	}

fun Interpreter.plusTakeEvaluation(rhs: Rhs): Evaluation<Interpreter> =
	rhs.valueOrThrow.functionOrThrow.evaluation.bind { function ->
		function.applyEvaluation(value).bind { output ->
			setEvaluation(output)
		}
	}

fun Interpreter.plusTextOrNullEvaluation(rhs: Rhs): Evaluation<Interpreter?> =
	rhs.valueOrNull?.resolveEmptyOrNull {
		value.resolvePrefixOrNull(valueName) {
			value(field(literal(it.string)))
		}
	}
		.evaluation
		.nullableBind { setEvaluation(it) }

fun Interpreter.plusBeEvaluation(rhs: Rhs): Evaluation<Interpreter> =
	setEvaluation(rhs.valueOrThrow)

fun Interpreter.plusEvaluation(be: Be): Evaluation<Interpreter> =
	dictionary.valueEvaluation(be.syntax).bind { setEvaluation(it) }

fun Interpreter.plusEvaluation(comment: Comment): Evaluation<Interpreter> =
	evaluation

fun Interpreter.plusDoEvaluation(rhs: Script): Evaluation<Interpreter> =
	dictionary.applyEvaluation(block(rhs), value).bind { setEvaluation(it) }

fun Interpreter.plusEvaluateEvaluation(rhs: Rhs): Evaluation<Interpreter> =
	dictionary.set(rhs.valueOrThrow).valueEvaluation(value.script).bind { evaluated ->
		setEvaluation(evaluated)
	}

fun Interpreter.plusExampleEvaluation(rhs: Rhs): Evaluation<Interpreter> =
	evaluation.also { rhs.valueOrThrow }

fun Interpreter.plusFailEvaluation(rhs: Script): Evaluation<Interpreter> =
	dictionary.valueEvaluation(value, rhs).bind { value ->
		evaluation.also { value.throwError() }
	}

fun Interpreter.plusTestEvaluation(test: Script): Evaluation<Interpreter> =
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

fun Interpreter.plusDoingOrNullEvaluation(rhs: Script): Evaluation<Interpreter?> =
	rhs.orNullIf(rhs.isEmpty).evaluation.nullableBind {
		plusEvaluation(field(dictionary.function(body(rhs))))
	}

fun Interpreter.plusHashOrNullEvaluation(rhs: Rhs): Evaluation<Interpreter?> =
	if (rhs.valueOrNull?.isEmpty == true) setEvaluation(value.hashValue)
	else evaluation(null)

fun Interpreter.plusIsEqualEvaluation(rhs: Script, negate: Boolean): Evaluation<Interpreter?> =
	dictionary.valueEvaluation(rhs).bind {
		setEvaluation(value.equals(it).isValue(negate))
	}

fun Interpreter.plusQuoteEvaluation(rhs: Script): Evaluation<Interpreter> =
	setEvaluation(value.script.plus(rhs).value)

fun Interpreter.plusSetEvaluation(rhs: Script): Evaluation<Interpreter> =
	dictionary.valueRhsEvaluation(rhs).bind { rhsValue ->
		setEvaluation(value.setOrThrow(rhsValue))
	}

fun Interpreter.plusSwitchEvaluation(rhs: Script): Evaluation<Interpreter> =
	dictionary.switchEvaluation(value, rhs).bind {
		setEvaluation(it)
	}

fun Interpreter.plusTraceOrNullEvaluation(rhs: Script): Evaluation<Interpreter?> =
	rhs
		.matchEmpty { traceValueEvaluation.bind { setEvaluation(it) } }
		?: evaluation(null)

fun Interpreter.plusTryEvaluation(rhs: Script): Evaluation<Interpreter> =
	dictionary.valueEvaluation(value, rhs)
		.bind { value -> setEvaluation(value(tryName fieldTo value(successName fieldTo value))) }
		.catch { throwable -> setEvaluation(value(tryName fieldTo throwable.value)) }

fun Interpreter.plusUpdateEvaluation(rhs: Script): Evaluation<Interpreter> =
	dictionary.updateEvaluation(value, rhs).bind { setEvaluation(it) }

fun Interpreter.plusDynamicEvaluation(scriptField: ScriptField): Evaluation<Interpreter> =
	dictionary.fieldEvaluation(scriptField).bind { field ->
		plusDynamicOrNullEvaluation(field).or {
			plusEvaluation(field)
		}
	}

fun Interpreter.plusEvaluation(literal: Literal): Evaluation<Interpreter> =
	plusEvaluation(field(literal))

fun Interpreter.plusEvaluation(field: Field): Evaluation<Interpreter> =
	dictionary.resolveEvaluation(value.plus(field)).bind {
		setEvaluation(it)
	}

fun Interpreter.plusAsEvaluation(rhs: Script): Evaluation<Interpreter> =
	plusEvaluation(as_(pattern(rhs)))

fun Interpreter.plusEvaluation(as_: As): Evaluation<Interpreter> =
	setEvaluation(value.as_(as_.pattern))

fun Interpreter.plusIsOrNullEvaluation(rhs: Script, negate: Boolean = false): Evaluation<Interpreter?> =
	rhs.onlyLineOrNull?.fieldOrNull.evaluation.nullableBind { field ->
		when (field.string) {
			equalName -> plusIsEqualEvaluation(field.rhs, negate)
			matchingName -> plusIsMatchingEvaluation(field.rhs, negate)
			// TODO: It's not working with "is not less than" and others
			notName -> plusIsOrNullEvaluation(field.rhs, negate.negate)
			else -> evaluation(null)
		}
	}

fun Interpreter.plusIsMatchingEvaluation(rhs: Script, negate: Boolean): Evaluation<Interpreter> =
	setEvaluation(value.isMatching(pattern(rhs), negate))

fun Interpreter.plusPrivateEvaluation(rhs: Script): Evaluation<Interpreter> =
	context.private.interpreterEvaluation(rhs).map { interpreter ->
		use(interpreter.context.publicDictionary)
	}

fun Interpreter.plusUseEvaluation(rhs: Script): Evaluation<Interpreter> =
	rhs.useOrNull.notNullOrThrow { value.plus(useName fieldTo rhs.value) }.evaluation.bind {
		plusEvaluation(it)
	}

fun Interpreter.plusValueOrNullEvaluation(rhs: Rhs): Evaluation<Interpreter?> =
	rhs.valueOrNull?.resolveEmptyOrNull {
		value.textOrNull?.let {
			value(valueName fieldTo it.scriptOrThrow.value)
		}
	}
		.evaluation
		.nullableBind { setEvaluation(it) }


fun Interpreter.plusWithEvaluation(rhs: Script): Evaluation<Interpreter> =
	dictionary.valueEvaluation(rhs).bind { rhsValue ->
		setEvaluation(value + rhsValue)
	}

val Interpreter.dictionary
	get() =
		context.privateDictionary

fun Interpreter.plusEvaluation(use: Use): Evaluation<Interpreter> =
	Evaluation { it.libraryEffect(use) }.map { use(it) }

fun Interpreter.use(dictionary: Dictionary): Interpreter =
	set(context.plusPrivate(dictionary))