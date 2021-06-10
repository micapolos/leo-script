package leo

import leo.base.fold
import leo.base.ifOrNull
import leo.base.notNullIf
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

fun Dictionary.applicationOrNull(value: Value): DefinitionApplication? =
	definitionStack.mapFirst { applicationOrNull(this, value) }

fun Dictionary.applicationOrNull(definition: Definition, value: Value): DefinitionApplication? =
	when (definition) {
		is LetDefinition -> applicationOrNull(definition.let, value)
		is RecursiveDefinition -> applicationOrNull(definition.recursive, value)
	}

fun Dictionary.applicationOrNull(let: DefinitionLet, value: Value): DefinitionApplication? =
	notNullIf(value.matches(let.value)) {
		DefinitionApplication(this, let.binding)
	}

fun Dictionary.applicationOrNull(recursive: LetRecursive, value: Value): DefinitionApplication? =
	recursive.dictionary.applicationOrNull(value)?.let { TODO() }

fun Dictionary.switchEvaluation(field: Field, cases: Value): Evaluation<Value> =
	when (cases) {
		EmptyValue -> value(switchName).throwError()
		is LinkValue -> switchEvaluation(field, cases.link)
	}

fun Dictionary.switchEvaluation(field: Field, link: Link): Evaluation<Value> =
	switchOrNullEvaluation(field, link.field).or {
		switchEvaluation(field, link.value)
	}

fun switchOrNullEvaluation(field: Field, case: Field): Evaluation<Value?> =
	ifOrNull(field.name == case.name) {
		value(case).nativeValue(doingName).functionOrThrow.applyEvaluation(value(field))
	} ?: evaluation(null)

fun Dictionary.evaluation(value: Value, switch: Switch): Evaluation<Value> =
	value.switchFieldOrThrow.let { field ->
		nullOf<Value>().evaluation.foldStateful(switch.caseSeq) { case ->
			if (this != null || field.name != case.name) evaluation
			else valueEvaluation(value(field), case.syntax)
		}.map { valueOrNull ->
			valueOrNull.notNullOrThrow { value(switchName) }
		}
	}

fun Dictionary.applyEvaluation(body: Body, given: Value): Evaluation<Value> =
	when (body) {
		is FnBody -> try {
			body.fn(value("the" fieldTo given)).evaluation
		} catch (throwable: Throwable) {
			throwable.value.failEvaluation()
		}
		is CodeBody -> applyEvaluation(body.block, given)
	}

fun Dictionary.applyEvaluation(block: Block, given: Value): Evaluation<Value> =
	when (block) {
		is RecursingBlock -> applyEvaluation(block.recursing, given)
		is SyntaxBlock -> applyEvaluation(block.syntax, given)
	}

fun Dictionary.applyEvaluation(repeat: Repeat, given: Value): Evaluation<Value> =
	given.evaluation.repeat { repeatingGiven ->
		valueEvaluation(repeatingGiven, repeat.syntax).bind { value ->
			value
				.resolvePrefixOrNull(endName) { it.endable(true).evaluation }
				?: value.endable(false).evaluation
		}
	}

fun Dictionary.applyEvaluation(recursing: Recursing, value: Value): Evaluation<Value> =
	plus(value).plusRecurse(recursing.syntax).valueEvaluation(recursing.syntax)

fun Dictionary.applyEvaluation(syntax: Syntax, given: Value): Evaluation<Value> =
	plus(given).valueEvaluation(syntax)

fun Dictionary.plusRecurse(syntax: Syntax): Dictionary =
	plus(
		definition(
			value(recurseName fieldTo anyValue),
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
	}

fun Dictionary.bindingEvaluation(be: Be): Evaluation<Binding> =
	valueEvaluation(be.syntax).map(::binding)

fun Dictionary.bindingEvaluation(do_: Do): Evaluation<Binding> =
	binding(body(do_.block)).evaluation

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
	valueEvaluation(equal.syntax).map { value(equalName fieldTo it) }

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

fun Dictionary.valueEvaluation(value: Value, give: Give): Evaluation<Value> =
	value.functionOrThrow.evaluation.bind { function ->
		valueEvaluation(give.syntax).bind { given ->
			function.applyEvaluation(given)
		}
	}

fun Dictionary.valueEvaluation(value: Value, take: Take): Evaluation<Value> =
	valueEvaluation(take.syntax).bind { taken ->
		taken.functionOrThrow.evaluation.bind { function ->
			function.applyEvaluation(value)
		}
	}

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

fun Dictionary.valueEvaluation(value: Value, bind: Bind): Evaluation<Value> =
	value.contentEvaluation.bind { content ->
		plus(content).valueEvaluation(bind.syntax)
	}

fun Dictionary.valueEvaluation(value: Value, end_: End): Evaluation<Value> =
	valueEvaluation(value, end_.syntax).map {
		value(endName fieldTo it)
	}

