package leo

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
		is CommentSyntaxLine -> plusEvaluation(line.comment)
		is DoSyntaxLine -> plusEvaluation(line.do_)
		is DoingSyntaxLine -> plusEvaluation(line.doing)
		is ExampleSyntaxLine -> plusEvaluation(line.example)
		is FailSyntaxLine -> plusEvaluation(line.fail)
		is GetSyntaxLine -> plusEvaluation(line.get)
		is IsSyntaxLine -> plusEvaluation(line.is_)
		is LetSyntaxLine -> plusEvaluation(line.let)
		is MatchingSyntaxLine -> plusEvaluation(line.matching)
		is PrivateSyntaxLine -> plusEvaluation(line.private)
		is RecurseSyntaxLine -> plusEvaluation(line.recurse)
		is RepeatSyntaxLine -> plusEvaluation(line.repeat)
		is QuoteSyntaxLine -> plusEvaluation(line.quote)
		is SetSyntaxLine -> plusEvaluation(line.set)
		is SwitchSyntaxLine -> plusEvaluation(line.switch)
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
		beName -> plusBeOrNullEvaluation(field.rhs)
		evaluateName -> plusEvaluateEvaluation(field.rhs)
		giveName -> plusGiveOrNullEvaluation(field.rhs)
		hashName -> plusHashOrNullEvaluation(field.rhs)
		takeName -> plusTakeOrNullEvaluation(field.rhs)
		textName -> plusTextOrNullEvaluation(field.rhs)
		valueName -> plusValueOrNullEvaluation(field.rhs)
		else -> evaluation(null)
	}

fun Evaluator.plusTakeOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	rhs.valueOrNull?.functionOrNull?.let { function ->
		function.applyEvaluation(value).bind { output ->
			setEvaluation(output)
		}
	}?:evaluation(null)

fun Evaluator.plusTextOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	rhs.valueOrNull?.resolveEmptyOrNull {
		value.resolvePrefixOrNull(valueName) {
			value(field(literal(it.string)))
		}
	}
		.evaluation
		.nullableBind { setEvaluation(it) }

fun Evaluator.plusBeOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	rhs.valueOrNull.evaluation.nullableBind {
		setEvaluation(it)
	}

fun Evaluator.plusEvaluation(comment: Comment): Evaluation<Evaluator> =
	evaluation

fun Evaluator.plusEvaluation(do_: Do): Evaluation<Evaluator> =
	dictionary.applyEvaluation(do_.block, value).bind { setEvaluation(it) }

fun Evaluator.plusEvaluateEvaluation(rhs: Rhs): Evaluation<Evaluator> =
	dictionary.set(rhs.valueOrThrow).valueEvaluation(value.script.syntax).bind { evaluated ->
		setEvaluation(evaluated)
	}

fun Evaluator.plusEvaluation(example: Example): Evaluation<Evaluator> =
	dictionary.valueEvaluation(example.syntax).bind { evaluation }

fun Evaluator.plusEvaluation(fail: Fail): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, fail.syntax).bind { value ->
		evaluation.also { value.throwError() }
	}

fun Evaluator.plusEvaluation(matching: Matching): Evaluation<Evaluator> =
	plusResolveEvaluation(matchingName fieldTo rhs(matching.pattern))

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

fun Evaluator.plusGiveOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	value.functionOrNull?.let { function ->
		rhs.valueOrNull?.let { given ->
			function.applyEvaluation(given).bind { output ->
				setEvaluation(output)
			}
		}
	}?:evaluation(null)

fun Evaluator.plusEvaluation(field: SyntaxField): Evaluation<Evaluator> =
	dictionary.fieldEvaluation(field).bind { field ->
		plusDynamicOrNullEvaluation(field).or {
			plusResolveEvaluation(field)
		}
	}

fun Evaluator.plusEvaluation(let: Let): Evaluation<Evaluator> =
	dictionary.bindingEvaluation(let.rhs).bind { binding ->
		set(context.plus(definition(let.pattern, binding))).evaluation
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
		.catch { throwable -> setEvaluation(value(tryName fieldTo throwable.value)) }

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
	setEvaluation(value.as_(as_.pattern))

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
	value.isMatching(matching.pattern).isValue.evaluation

fun Evaluator.isValueEvaluation(syntax: Syntax): Evaluation<Value> =
	dictionary.valueEvaluation(syntax).bind { rhsValue ->
		dictionary.resolveEvaluation(value.plus(isName fieldTo rhsValue))
	}

fun Evaluator.plusEvaluation(private: Private): Evaluation<Evaluator> =
	context.private.evaluatorEvaluation(private.syntax).map { evaluator ->
		use(evaluator.context.publicDictionary)
	}

fun Evaluator.plusEvaluation(recurse: Recurse): Evaluation<Evaluator> =
	dictionary.valueEvaluation(recurse.syntax).bind { repeatValue ->
		plusResolveEvaluation(recurseName fieldTo repeatValue)
	}

fun Evaluator.plusEvaluation(repeat: Repeat): Evaluation<Evaluator> =
	dictionary.valueEvaluation(repeat.syntax).bind { repeatValue ->
		plusResolveEvaluation(repeatName fieldTo repeatValue)
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