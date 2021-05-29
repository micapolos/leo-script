package leo25

import leo.base.fold
import leo.base.orNullIf
import leo.base.reverse
import leo13.base.negate
import leo14.FieldScriptLine
import leo14.Literal
import leo14.LiteralScriptLine
import leo14.Script
import leo14.ScriptField
import leo14.ScriptLine
import leo14.fieldOrNull
import leo14.isEmpty
import leo14.lineSeq
import leo14.literal
import leo14.matchEmpty
import leo14.matchInfix
import leo14.onlyLineOrNull
import leo14.plus
import leo25.parser.scriptOrThrow
import leo25.prelude.preludeDictionary

data class Interpreter(
	val context: Context,
	val value: Value
)

fun Context.interpreter(value: Value = value()) =
	Interpreter(this, value)

fun Interpreter.setLeo(value: Value): Leo<Interpreter> =
	context.interpreter(value).leo

fun Interpreter.set(context: Context): Interpreter =
	context.interpreter(value)

fun Dictionary.valueLeo(script: Script): Leo<Value> =
	context.interpreterLeo(script).map { it.value }

fun Dictionary.valueLeo(value: Value, script: Script): Leo<Value> =
	context.interpreter(value).plusLeo(script).map { it.value }

fun Dictionary.fieldsValueLeo(script: Script): Leo<Value> =
	value().leo.fold(script.lineSeq.reverse) { line ->
		bind { value ->
			fieldLeo(line).bind { field ->
				value.plus(field).leo
			}
		}
	}

fun Dictionary.fieldLeo(scriptLine: ScriptLine): Leo<Field> =
	when (scriptLine) {
		is FieldScriptLine -> fieldLeo(scriptLine.field)
		is LiteralScriptLine -> field(scriptLine.literal).leo
	}

fun Dictionary.fieldLeo(scriptField: ScriptField): Leo<Field> =
	valueLeo(scriptField.rhs).map {
		scriptField.string fieldTo it
	}

val String.dictionary: Dictionary
	get() =
		scriptOrThrow.dictionary

val Script.dictionary: Dictionary
	get() =
		preludeDictionary.context.interpreter().plusLeo(this).get.context.publicDictionary

fun Context.interpreterLeo(script: Script): Leo<Interpreter> =
	interpreter().plusLeo(script)

fun Interpreter.plusLeo(script: Script): Leo<Interpreter> =
	leo.fold(script.lineSeq.reverse) { line ->
		bind {
			it.plusLeo(line)
		}
	}

fun Interpreter.plusLeo(scriptLine: ScriptLine): Leo<Interpreter> =
	when (scriptLine) {
		is FieldScriptLine -> plusLeo(scriptLine.field)
		is LiteralScriptLine -> plusLeo(scriptLine.literal)
	}

fun Interpreter.plusLeo(scriptField: ScriptField): Leo<Interpreter> =
	plusDefinitionsOrNullLeo(scriptField).or {
		plusStaticOrNullLeo(scriptField).or {
			plusDynamicLeo(scriptField)
		}
	}

fun Interpreter.plusDefinitionsOrNullLeo(scriptField: ScriptField): Leo<Interpreter?> =
	dictionary.definitionSeqOrNullLeo(scriptField).nullableMap { definitionSeq ->
		set(context.fold(definitionSeq.reverse) { plus(it) })
	}

fun Interpreter.plusStaticOrNullLeo(scriptField: ScriptField): Leo<Interpreter?> =
	when (scriptField.string) {
		asName -> plusAsLeo(scriptField.rhs)
		commentName -> leo
		doName -> plusDoLeo(scriptField.rhs)
		doingName -> plusDoingOrNullLeo(scriptField.rhs)
		failName -> plusFailLeo(scriptField.rhs)
		isName -> plusIsOrNullLeo(scriptField.rhs)
		privateName -> plusPrivateLeo(scriptField.rhs)
		quoteName -> plusQuoteLeo(scriptField.rhs)
		setName -> plusSetLeo(scriptField.rhs)
		switchName -> plusSwitchLeo(scriptField.rhs)
		testName -> plusTestLeo(scriptField.rhs)
		traceName -> plusTraceOrNullLeo(scriptField.rhs)
		tryName -> plusTryLeo(scriptField.rhs)
		updateName -> plusUpdateLeo(scriptField.rhs)
		useName -> plusUseLeo(scriptField.rhs)
		withName -> plusWithLeo(scriptField.rhs)
		else -> leo(null)
	}

fun Interpreter.plusDynamicOrNullLeo(field: Field): Leo<Interpreter?> =
	when (field.name) {
		giveName -> plusApplyLeo(field.rhs)
		beName -> plusBeLeo(field.rhs)
		evaluateName -> plusEvaluateLeo(field.rhs)
		exampleName -> plusExampleLeo(field.rhs)
		hashName -> plusHashOrNullLeo(field.rhs)
		takeName -> plusTakeLeo(field.rhs)
		textName -> plusTextOrNullLeo(field.rhs)
		valueName -> plusValueOrNullLeo(field.rhs)
		else -> leo(null)
	}

fun Interpreter.plusApplyLeo(rhs: Rhs): Leo<Interpreter> =
	value.functionOrThrow.leo.bind { function ->
		function.applyLeo(rhs.valueOrThrow).bind { output ->
			setLeo(output)
		}
	}

fun Interpreter.plusTakeLeo(rhs: Rhs): Leo<Interpreter> =
	rhs.valueOrThrow.functionOrThrow.leo.bind { function ->
		function.applyLeo(value).bind { output ->
			setLeo(output)
		}
	}

fun Interpreter.plusTextOrNullLeo(rhs: Rhs): Leo<Interpreter?> =
	rhs.valueOrNull?.resolveEmptyOrNull {
		value.resolvePrefixOrNull(valueName) {
			value(field(literal(it.string)))
		}
	}
		.leo
		.nullableBind { setLeo(it) }

fun Interpreter.plusBeLeo(rhs: Rhs): Leo<Interpreter> =
	setLeo(rhs.valueOrThrow)

fun Interpreter.plusDoLeo(rhs: Script): Leo<Interpreter> =
	dictionary.applyLeo(block(rhs), value).bind { setLeo(it) }

fun Interpreter.plusEvaluateLeo(rhs: Rhs): Leo<Interpreter> =
	dictionary.set(rhs.valueOrThrow).valueLeo(value.script).bind { evaluated ->
		setLeo(evaluated)
	}

fun Interpreter.plusExampleLeo(rhs: Rhs): Leo<Interpreter> =
	leo.also { rhs.valueOrThrow }

fun Interpreter.plusFailLeo(rhs: Script): Leo<Interpreter> =
	dictionary.valueLeo(value, rhs).bind { value ->
		leo.also { value.throwError() }
	}

fun Interpreter.plusTestLeo(test: Script): Leo<Interpreter> =
	test.matchInfix(isName) { lhs, rhs ->
		dictionary.valueLeo(test).bind { result ->
			when (result) {
				true.isValue -> leo
				false.isValue ->
					dictionary.valueLeo(lhs).bind { lhs ->
						dictionary.valueLeo(rhs).bind { rhs ->
							leo.also {
								value(testName fieldTo test.value)
									.plus(
										causeName fieldTo
											lhs.plus(isName fieldTo value(notName fieldTo rhs))
									).throwError()
							}
						}
					}
				else -> leo.also {
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

fun Interpreter.plusDoingOrNullLeo(rhs: Script): Leo<Interpreter?> =
	rhs.orNullIf(rhs.isEmpty).leo.nullableBind {
		plusLeo(field(dictionary.function(body(rhs))))
	}

fun Interpreter.plusHashOrNullLeo(rhs: Rhs): Leo<Interpreter?> =
	if (rhs.valueOrNull?.isEmpty == true) setLeo(value.hashValue)
	else leo(null)

fun Interpreter.plusIsEqualLeo(rhs: Script, negate: Boolean): Leo<Interpreter?> =
	dictionary.valueLeo(rhs).bind {
		setLeo(value.equals(it).isValue(negate))
	}

fun Interpreter.plusQuoteLeo(rhs: Script): Leo<Interpreter> =
	setLeo(value.script.plus(rhs).value)

fun Interpreter.plusSetLeo(rhs: Script): Leo<Interpreter> =
	dictionary.fieldsValueLeo(rhs).bind { rhsValue ->
		setLeo(value.setOrThrow(rhsValue))
	}

fun Interpreter.plusSwitchLeo(rhs: Script): Leo<Interpreter> =
	dictionary.switchLeo(value, rhs).bind {
		setLeo(it)
	}

fun Interpreter.plusTraceOrNullLeo(rhs: Script): Leo<Interpreter?> =
	rhs
		.matchEmpty { traceValueLeo.bind { setLeo(it) } }
		?: leo(null)

fun Interpreter.plusTryLeo(rhs: Script): Leo<Interpreter> =
	dictionary.valueLeo(value, rhs)
		.bind { value -> setLeo(value(tryName fieldTo value(successName fieldTo value))) }
		.catch { throwable -> setLeo(value(tryName fieldTo throwable.value)) }

fun Interpreter.plusUpdateLeo(rhs: Script): Leo<Interpreter> =
	dictionary.updateLeo(value, rhs).bind { setLeo(it) }

fun Interpreter.plusDynamicLeo(scriptField: ScriptField): Leo<Interpreter> =
	dictionary.fieldLeo(scriptField).bind { field ->
		plusDynamicOrNullLeo(field).or {
			plusLeo(field)
		}
	}

fun Interpreter.plusLeo(literal: Literal): Leo<Interpreter> =
	plusLeo(field(literal))

fun Interpreter.plusLeo(field: Field): Leo<Interpreter> =
	dictionary.resolveLeo(value.plus(field)).bind {
		setLeo(it)
	}

fun Interpreter.plusAsLeo(rhs: Script): Leo<Interpreter> =
	setLeo(value.as_(pattern(rhs)))

fun Interpreter.plusIsOrNullLeo(rhs: Script, negate: Boolean = false): Leo<Interpreter?> =
	rhs.onlyLineOrNull?.fieldOrNull.leo.nullableBind { field ->
		when (field.string) {
			equalName -> plusIsEqualLeo(field.rhs, negate)
			matchingName -> plusIsMatchingLeo(field.rhs, negate)
			// TODO: It's not working with "is not less than" and others
			notName -> plusIsOrNullLeo(field.rhs, negate.negate)
			else -> leo(null)
		}
	}

fun Interpreter.plusIsMatchingLeo(rhs: Script, negate: Boolean): Leo<Interpreter> =
	setLeo(value.isMatching(pattern(rhs), negate))

fun Interpreter.plusPrivateLeo(rhs: Script): Leo<Interpreter> =
	context.private.interpreterLeo(rhs).map { interpreter ->
		use(interpreter.context.publicDictionary)
	}

fun Interpreter.plusUseLeo(rhs: Script): Leo<Interpreter> =
	rhs.useOrNull.notNullOrThrow { value.plus(useName fieldTo rhs.value) }.leo.bind {
		plusLeo(it)
	}

fun Interpreter.plusValueOrNullLeo(rhs: Rhs): Leo<Interpreter?> =
	rhs.valueOrNull?.resolveEmptyOrNull {
		value.textOrNull?.let {
			value(valueName fieldTo it.scriptOrThrow.value)
		}
	}
		.leo
		.nullableBind { setLeo(it) }


fun Interpreter.plusWithLeo(rhs: Script): Leo<Interpreter> =
	dictionary.valueLeo(rhs).bind { rhsValue ->
		setLeo(value + rhsValue)
	}

val Interpreter.dictionary
	get() =
		context.privateDictionary

fun Interpreter.plusLeo(use: Use): Leo<Interpreter> =
	Leo { it.libraryEffect(use) }.map { use(it) }

fun Interpreter.use(dictionary: Dictionary): Interpreter =
	set(context.plusPrivate(dictionary))