package leo

import leo.base.fold
import leo.base.ifOrNull
import leo.base.nullOf
import leo.base.runIf
import leo.natives.nativeValue

@kotlin.jvm.JvmInline
value class Dictionary(val definitionStack: Stack<Definition>)

fun dictionary(vararg definitions: Definition): Dictionary =
	Dictionary(stack(*definitions))

fun Dictionary.plus(definition: Definition): Dictionary =
	Dictionary(definitionStack.push(definition))

operator fun Dictionary.plus(dictionary: Dictionary): Dictionary =
	Dictionary(definitionStack.pushAll(dictionary.definitionStack))

val Dictionary.givenOrNull: ValueGiven? get() =
	definitionStack.mapFirst { (this as? GivenDefinition)?.given }

fun Dictionary.selectEvaluation(field: Field, cases: Value): Evaluation<Value> =
	when (cases) {
		EmptyValue -> value(selectName).throwError()
		is LinkValue -> selectEvaluation(field, cases.link)
	}

fun Dictionary.selectEvaluation(field: Field, link: Link): Evaluation<Value> =
	selectOrNullEvaluation(field, link.field).or {
		selectEvaluation(field, link.value)
	}

fun selectOrNullEvaluation(field: Field, case: Field): Evaluation<Value?> =
	ifOrNull(field.name == case.name) {
		value(case).nativeValue(doingName).functionOrThrow.giveEvaluation(value(field))
	} ?: evaluation(null)

fun Dictionary.evaluation(value: Value, select: Select): Evaluation<Value> =
	value.selectFieldOrThrow.let { field ->
		nullOf<Value>().evaluation.foldStateful(select.caseSeq) { case ->
			if (this != null || field.name != case.name) evaluation
			else valueEvaluation(case.syntax).bind { caseValue ->
				caseValue.functionOrThrow.giveEvaluation(value(field))
			}
		}.map { valueOrNull ->
			valueOrNull.notNullOrThrow { value(selectName) }
		}
	}

fun Dictionary.applyEvaluation(value: Value, binder: Binder): Evaluation<Value> =
	when (binder) {
		is ApplyingBinder -> applyEvaluation(value, binder.applying.body)
		is BeingBinder -> binder.being.value.evaluation
		is CombiningBinder -> applyEvaluation(value, binder.combining.body)
		is DoingBinder -> plus(given(value)).applyEvaluation(value(), binder.doing.body)
		is HavingBinder -> value.have(binder.having.value).evaluation
	}

fun Dictionary.applyEvaluation(value: Value, body: Body): Evaluation<Value> =
	when (body) {
		is FnBody -> applyEvaluation(value, body.fn)
		is BlockBody -> giveEvaluation(value, body.block)
	}

fun applyEvaluation(value: Value, fn: (Value) -> Value): Evaluation<Value> =
	try {
		fn(value("the" fieldTo value)).evaluation
	} catch (e: DebugError) {
		throw e
	} catch (throwable: Throwable) {
		throwable.value.failEvaluation()
	}

fun Dictionary.giveEvaluation(value: Value, block: Block): Evaluation<Value> =
	when (block) {
		is RecursingBlock -> applyEvaluation(value, block.recursing)
		is SyntaxBlock -> applyEvaluation(value, block.syntax)
	}

fun Dictionary.applyEvaluation(repeat: Repeat, given: Value): Evaluation<Value> =
	given.evaluation.repeat { repeatingGiven ->
		valueEvaluation(repeatingGiven, repeat.syntax).bind { value ->
			value
				.resolvePrefixOrNull(endName) { it.endable(true).evaluation }
				?: value.endable(false).evaluation
		}
	}

fun Dictionary.applyEvaluation(value: Value, recursing: Recursing): Evaluation<Value> =
	// TODO: Recursing must go before binder.
	plusRecurse(recursing.syntax).applyEvaluation(value, binder(doing(body(block(recursing.syntax)))))

fun Dictionary.applyEvaluation(given: Value, syntax: Syntax): Evaluation<Value> =
	context.evaluator(given).plusEvaluation(syntax).map { it.value }

fun Dictionary.plusRecurse(syntax: Syntax): Dictionary =
	plus(
		definition(
			value(recurseName fieldTo anythingValue),
			binding(recurse(body(block(recursing(syntax)))))
		)
	)

fun Dictionary.evaluation(value: Value, update: Update): Evaluation<Value> =
	value.structureOrThrow.let { structure ->
		structureEvaluation(structure.value, update).map { rhs ->
			value(structure.name fieldTo rhs)
		}
	}

fun Dictionary.structureEvaluation(value: Value, update: Update): Evaluation<Value> =
	value.evaluation.fold(update.fieldSeq) { field ->
		bind { value ->
			structureEvaluation(value, field)
		}
	}

fun Dictionary.structureEvaluation(value: Value, syntaxField: SyntaxField): Evaluation<Value> =
	when (value) {
		EmptyValue -> value("no" fieldTo value("field" fieldTo value(syntaxField.name))).throwError()
		is LinkValue -> structureEvaluation(value.link, syntaxField).map { value(it) }
	}

fun Dictionary.structureEvaluation(link: Link, syntaxField: SyntaxField): Evaluation<Link> =
	if (link.field.name == syntaxField.name)
		link.field.rhs.valueOrNull.notNullOrThrow { value(link) }.evaluation.bind { rhs ->
			context.evaluator(rhs).plusEvaluation(syntaxField.rhsSyntax).map { rhsValue ->
				(link.value linkTo (syntaxField.name fieldTo rhsValue.value))
			}
		}
	else structureEvaluation(link.value, syntaxField).map { it linkTo link.field }

fun Dictionary.bindingEvaluation(rhs: LetRhs): Evaluation<Binding> =
	when (rhs) {
		is BeLetRhs -> bindingEvaluation(rhs.be)
		is DoLetRhs -> bindingEvaluation(rhs.do_)
		is HaveLetRhs -> bindingEvaluation(rhs.have)
		is ApplyLetRhs -> bindingEvaluation(rhs.apply)
	}

fun Dictionary.bindingEvaluation(be: Be): Evaluation<Binding> =
	valueEvaluation(be.syntax).map(::binding)

fun Dictionary.bindingEvaluation(do_: Do): Evaluation<Binding> =
	binding(binder(doing(body(do_.block)))).evaluation

fun Dictionary.bindingEvaluation(have: Have): Evaluation<Binding> =
	valueEvaluation(have.syntax).map { binding(binder(having(it))) }

fun Dictionary.bindingEvaluation(apply: Apply): Evaluation<Binding> =
	binding(binder(applying(body(apply.block)))).evaluation

fun Dictionary.evaluation(value: Value, set: Set): Evaluation<Value> =
	value().evaluation.foldStateful(set.atomSeq) { atom ->
		fieldEvaluation(atom).map { field ->
			plus(field)
		}
	}.map { value.setOrThrow(it) }

fun Dictionary.fieldEvaluation(is_: Is): Evaluation<Field> =
	valueEvaluation(is_.rhs).map { rhs ->
		isName fieldTo rhs.runIf(is_.negated) { value(notName fieldTo this) }
	}

fun Dictionary.valueEvaluation(rhs: IsRhs): Evaluation<Value> =
	when (rhs) {
		is EqualIsRhs -> valueEvaluation(rhs.equal)
		is MatchingIsRhs -> valueEvaluation(rhs.matching)
		is SyntaxIsRhs -> valueEvaluation(rhs.syntax)
	}

fun Dictionary.valueEvaluation(equal: Equal): Evaluation<Value> =
	valueEvaluation(equal.syntax).map { value(equalName fieldTo value(toName fieldTo it)) }

fun Dictionary.valueEvaluation(matching: Matching): Evaluation<Value> =
	value(matchingName fieldTo matching.syntax.script.value).evaluation

fun Dictionary.valueEvaluation(value: Value, recurse: Recurse): Evaluation<Value> =
	valueEvaluation(value, recurse.syntax).bind { recursedValue ->
		resolveEvaluation(value(recurseName fieldTo recursedValue))
	}

fun Dictionary.valueEvaluation(value: Value, try_: Try): Evaluation<Value> =
	valueEvaluation(value, try_.syntax)
		.bind { triedValue -> value(tryName fieldTo value(successName fieldTo triedValue)).evaluation }
		.catch { throwable -> value(tryName fieldTo throwable.value.errorValue).evaluation }

fun Dictionary.valueEvaluation(value: Value, fail: Fail): Evaluation<Value> =
	valueEvaluation(value, fail.syntax).bind {
		it.failEvaluation()
	}

fun Dictionary.fieldEvaluation(matching: Matching): Evaluation<Field> =
	valueEvaluation(matching.syntax).map {
		matchingName fieldTo rhs(it)
	}

fun Dictionary.unitEvaluation(test: Test): Evaluation<Unit> =
	valueEvaluation(test.syntax).bind { result ->
		if (result.isBoolean) Unit.evaluation
		else valueEvaluation(test.lhsSyntax).bind { lhs ->
			fieldEvaluation(test.is_.negate).bind { isField ->
				Unit.evaluation.also {
					value(testName fieldTo test.script.value)
						.plus(causeName fieldTo lhs.plus(isField))
						.throwError()
				}
			}
		}
	}

fun Dictionary.giveValueEvaluationOrNull(value: Value, giveValue: Value): Evaluation<Value>? =
	value.functionOrNull?.giveEvaluation(giveValue)

fun Dictionary.takeValueEvaluationOrNull(value: Value, takeValue: Value): Evaluation<Value>? =
	takeValue.functionOrNull?.giveEvaluation(value)

fun Dictionary.definitionEvaluation(let: Let): Evaluation<Definition> =
	valueEvaluation(let.syntax).bind { letValue ->
		bindingEvaluation(let.rhs).map { binding ->
			definition(letValue, binding)
		}
	}

fun Dictionary.valueEvaluation(value: Value, with: With): Evaluation<Value> =
	valueEvaluation(with.syntax).map { value + it }

fun Dictionary.valueEvaluation(value: Value, as_: As): Evaluation<Value> =
	valueEvaluation(as_.syntax).map { value.as_(it) }

fun Dictionary.valueEvaluation(value: Value, apply: Apply): Evaluation<Value> =
	giveEvaluation(value, apply.block)

fun Dictionary.valueEvaluation(value: Value, end_: End): Evaluation<Value> =
	valueEvaluation(value, end_.syntax).map {
		value(endName fieldTo it)
	}

