package leo

import leo.base.ifOrNull
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

fun Dictionary.valueEvaluation(value: Value, syntax: Syntax): Evaluation<Value> =
	context.evaluator(value).plusEvaluation(syntax).map { it.value }

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
		syntax.dictionary

val Syntax.dictionary: Dictionary
	get() =
		preludeDictionary.context.evaluator().plusEvaluation(this).get.context.publicDictionary

fun Context.evaluatorEvaluation(syntax: Syntax): Evaluation<Evaluator> =
	evaluator().plusEvaluation(syntax)

fun Dictionary.valueEvaluation(syntax: Syntax): Evaluation<Value> =
	context.evaluator(value()).plusEvaluation(syntax).map { it.value }

fun Evaluator.plusEvaluation(syntax: Syntax): Evaluation<Evaluator> =
	evaluation.foldStateful(syntax.lineSeq) { plusEvaluation(it) }

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

fun Evaluator.plusEvaluation(atom: SyntaxAtom): Evaluation<Evaluator> =
	when (atom) {
		is FieldSyntaxAtom -> plusEvaluation(atom.field)
		is LiteralSyntaxAtom -> plusEvaluation(atom.literal)
	}

fun Evaluator.plusDynamicOrNullEvaluation(field: Field): Evaluation<Evaluator?> =
	when (field.name) {
		contentName -> plusContentOrNullEvaluation(field.rhs)
		evaluateName -> plusEvaluateEvaluation(field.rhs)
		hashName -> plusHashOrNullEvaluation(field.rhs)
		textName -> plusTextOrNullEvaluation(field.rhs)
		valueName -> plusValueOrNullEvaluation(field.rhs)
		else -> evaluation(null)
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

fun Evaluator.plusEvaluation(be: Be): Evaluation<Evaluator> =
	dictionary.valueEvaluation(be.syntax).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(@Suppress("UNUSED_PARAMETER") comment: Comment): Evaluation<Evaluator> =
	evaluation

fun Evaluator.plusEvaluation(do_: Do): Evaluation<Evaluator> =
	dictionary.applyEvaluation(do_.block, value).bind { setEvaluation(it) }

fun Evaluator.plusContentOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	ifOrNull(rhs.isEmpty) {
		value.structureOrNull?.value?.let { setEvaluation(it) }
	} ?: evaluation(null)

fun Evaluator.plusEvaluateEvaluation(rhs: Rhs): Evaluation<Evaluator> =
	dictionary.set(rhs.valueOrThrow).valueEvaluation(value.script.syntax).bind { evaluated ->
		setEvaluation(evaluated)
	}

fun Evaluator.plusEvaluation(example: Example): Evaluation<Evaluator> =
	dictionary.valueEvaluation(example.syntax).bind { evaluation }

fun Evaluator.plusEvaluation(fail: Fail): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, fail.syntax).bind { value ->
		value.failEvaluation()
	}

fun Evaluator.plusEvaluation(matching: Matching): Evaluation<Evaluator> =
	plusResolveEvaluation(matchingName fieldTo valueRhs(matching.type))

fun Evaluator.plusEvaluation(test: Test): Evaluation<Evaluator> =
	dictionary.valueEvaluation(test.syntax).bind { result ->
		if (result.isBoolean) evaluation
		else dictionary.valueEvaluation(test.lhsSyntax).bind { lhs ->
			dictionary.fieldEvaluation(test.is_.negate).bind { isField ->
				evaluation.also {
					value(testName fieldTo test.script.value)
						.plus(causeName fieldTo lhs.plus(isField))
						.throwError()
				}
			}
		}
	}

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


fun Evaluator.plusEvaluation(syntaxField: SyntaxField): Evaluation<Evaluator> =
	dictionary.fieldEvaluation(syntaxField).bind { field ->
		plusDynamicOrNullEvaluation(field).or {
			plusResolveEvaluation(field)
		}
	}

fun Evaluator.plusEvaluation(let: Let): Evaluation<Evaluator> =
	dictionary.bindingEvaluation(let.rhs).bind { binding ->
		set(context.plus(definition(let.type, binding))).evaluation
	}

fun Evaluator.plusEvaluation(doing: Doing): Evaluation<Evaluator> =
	plusResolveEvaluation(field(dictionary.function(body(doing.block))))

fun Evaluator.plusHashOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	if (rhs.valueOrNull?.isEmpty == true) setEvaluation(value.hashValue)
	else evaluation(null)

fun Evaluator.plusEvaluation(quote: Quote): Evaluation<Evaluator> =
	setEvaluation(value.plus(quote.script.value))

fun Evaluator.plusEvaluation(switch: Switch): Evaluation<Evaluator> =
	dictionary.evaluation(value, switch).bind {
		setEvaluation(it)
	}

fun Evaluator.plusEvaluation(try_: Try): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, try_.syntax)
		.bind { value -> setEvaluation(value(tryName fieldTo value(successName fieldTo value))) }
		.catch { throwable -> setEvaluation(value(tryName fieldTo throwable.value.errorValue)) }

fun Evaluator.plusEvaluation(update: Update): Evaluation<Evaluator> =
	dictionary.evaluation(value, update).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(set: Set): Evaluation<Evaluator> =
	dictionary.evaluation(value, set).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(literal: Literal): Evaluation<Evaluator> =
	plusResolveEvaluation(field(literal))

fun Evaluator.plusResolveEvaluation(field: Field): Evaluation<Evaluator> =
	dictionary.resolveEvaluation(value.plus(field)).bind {
		setEvaluation(it)
	}

fun Evaluator.plusEvaluation(as_: As): Evaluation<Evaluator> =
	setEvaluation(value.as_(as_.type))

fun Evaluator.plusEvaluation(is_: Is): Evaluation<Evaluator> =
	isValueEvaluation(is_.rhs).bind { isValue ->
		setEvaluation(isValue.runIf(is_.negated) { isNegate })
	}

fun Evaluator.isValueEvaluation(rhs: IsRhs): Evaluation<Value> =
	when (rhs) {
		is EqualIsRhs -> isValueEvaluation(rhs.equal)
		is MatchingIsRhs -> isValueEvaluation(rhs.matching)
		is SyntaxIsRhs -> isValueEvaluation(rhs.syntax)
	}

fun Evaluator.isValueEvaluation(equal: Equal): Evaluation<Value> =
	dictionary.valueEvaluation(equal.syntax).map {
		(value == it).isValue
	}

fun Evaluator.isValueEvaluation(matching: Matching): Evaluation<Value> =
	value.matches(matching.type).isValue.evaluation

fun Evaluator.isValueEvaluation(syntax: Syntax): Evaluation<Value> =
	dictionary.valueEvaluation(syntax).bind { rhsValue ->
		dictionary.resolveEvaluation(value.plus(isName fieldTo rhsValue))
	}

fun Evaluator.plusEvaluation(private: Private): Evaluation<Evaluator> =
	context.private.evaluatorEvaluation(private.syntax).map { evaluator ->
		use(evaluator.context.publicDictionary)
	}

fun Evaluator.plusEvaluation(recurse: Recurse): Evaluation<Evaluator> =
	if (recurse.atomOrNull == null) plusResolveEvaluation(recurseName fieldTo value())
	else dictionary.fieldEvaluation(recurse.atomOrNull).bind { repeatField ->
		dictionary.resolveEvaluation(value.plus(repeatField).plus(recurseName fieldTo value())).bind {
			setEvaluation(it)
		}
	}

fun Evaluator.plusEvaluation(repeat: Repeat): Evaluation<Evaluator> =
	if (repeat.atomOrNull == null) setEvaluation(value(repeatName fieldTo value))
	else dictionary.fieldEvaluation(repeat.atomOrNull).bind { repeatField ->
		setEvaluation(value(repeatName fieldTo value.plus(repeatField)))
	}

fun Evaluator.plusValueOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	rhs.valueOrNull?.resolveEmptyOrNull {
		value.textOrNull?.let {
			value(valueName fieldTo it.scriptOrThrow.value)
		}
	}
		.evaluation
		.nullableBind { setEvaluation(it) }


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