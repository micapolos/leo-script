package leo

import leo.base.fold
import leo.base.ifOrNull
import leo.base.nullOf
import leo.base.runIf
import leo.natives.nativeValue

data class Dictionary(val definitionStack: Stack<Definition>)

fun dictionary(vararg definitions: Definition): Dictionary =
	Dictionary(stack(*definitions))

fun Dictionary.plus(definition: Definition): Dictionary =
	Dictionary(definitionStack.push(definition))

//fun Dictionary.plus(script: Script, body: Body): Dictionary =
//	plus(definition(script.type, binding(function(body))))

operator fun Dictionary.plus(dictionary: Dictionary): Dictionary =
	Dictionary(definitionStack.pushAll(dictionary.definitionStack))

fun Dictionary.bindingOrNull(value: Value): Binding? =
	definitionStack
		.first { definition -> value.matches(definition.value) }
		?.binding

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
			body.fn(bind(given)).evaluation
		} catch (throwable: Throwable) {
			throwable.value.failEvaluation()
		}
		is CodeBody -> applyEvaluation(body.code, given)
	}

fun Dictionary.applyEvaluation(code: Code, given: Value): Evaluation<Value> =
	when (code) {
		is RecursingCode -> applyEvaluation(code.recursing, given)
		is RepeatingCode -> applyEvaluation(code.repeating, given)
		is SyntaxCode -> applyEvaluation(code.syntax, given)
	}

fun Dictionary.applyEvaluation(repeating: Repeating, given: Value): Evaluation<Value> =
	given.evaluation.valueBindRepeating { repeatingGiven ->
		valueEvaluation(repeatingGiven, repeating.syntax)
	}

fun Dictionary.applyEvaluation(recursing: Recursing, given: Value): Evaluation<Value> =
	plusRecurse(recursing.syntax).valueEvaluation(given, recursing.syntax)

fun Dictionary.applyEvaluation(syntax: Syntax, given: Value): Evaluation<Value> =
	valueEvaluation(given, syntax)

fun Dictionary.plusRecurse(syntax: Syntax): Dictionary =
	plus(
		definition(
			anyValue.plus(recurseName fieldTo value()),
			binding(function(body(code(recursing(syntax)))))
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
	binding(function(body(do_.code))).evaluation

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
