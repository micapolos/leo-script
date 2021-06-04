package leo

import leo.base.orNullIf
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

val Syntax.dictionaryEvaluation: Evaluation<Dictionary>
	get() =
		preludeDictionary.context.evaluator()
			.plusEvaluation(this)
			.map { it.context.publicDictionary }

fun Context.evaluatorEvaluation(syntax: Syntax): Evaluation<Evaluator> =
	evaluator().plusEvaluation(syntax)

fun Dictionary.valueEvaluation(syntax: Syntax): Evaluation<Value> =
	context.evaluator(value()).plusEvaluation(syntax).map { it.value }

fun Evaluator.plusEvaluation(syntax: Syntax): Evaluation<Evaluator> =
	evaluation.foldStateful(syntax.lineSeq) { plusEvaluation(it) }

fun Evaluator.plusEvaluation(line: SyntaxLine): Evaluation<Evaluator> =
	when (line) {
		is AsSyntaxLine -> plusEvaluation(line.as_)
		is AnySyntaxLine -> plusEvaluation(line.any)
		is AtomSyntaxLine -> plusEvaluation(line.atom)
		is BeSyntaxLine -> plusEvaluation(line.be)
		is BindSyntaxLine -> plusEvaluation(line.bind)
		is BreakSyntaxLine -> plusEvaluation(line.break_)
		is CheckSyntaxLine -> plusEvaluation(line.check)
		is CommentSyntaxLine -> plusEvaluation(line.comment)
		is DoSyntaxLine -> plusEvaluation(line.do_)
		is DoingSyntaxLine -> plusEvaluation(line.doing)
		is ExampleSyntaxLine -> plusEvaluation(line.example)
		is FailSyntaxLine -> plusEvaluation(line.fail)
		is GetSyntaxLine -> plusEvaluation(line.get)
		is GiveSyntaxLine -> plusEvaluation(line.give)
		is IsSyntaxLine -> plusEvaluation(line.is_)
		is LetSyntaxLine -> plusEvaluation(line.let)
		is LoopSyntaxLine -> plusEvaluation(line.loop)
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
		evaluateName -> plusEvaluateOrNullEvaluation(field.rhs)
		hashName -> plusHashOrNullEvaluation(field.rhs)
		textName -> plusTextOrNullEvaluation(field.rhs)
		valueName -> plusValueOrNullEvaluation(field.rhs)
		else -> evaluation(null)
	}

fun Evaluator.plusEvaluation(take: Take): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, take).bind { setEvaluation(it) }

fun Evaluator.plusTextOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	value.orNullIf { !isEmpty }.let {
		rhs.valueOrNull?.resolvePrefixOrNull { name, content ->
			when (name) {
				valueName -> value(field(literal(content.string)))
				nameName -> content.fieldOrNull?.name?.let { value(field(literal(it))) }
				else -> null
			}
		}
	}
		.evaluation
		.nullableBind { setEvaluation(it) }

fun Evaluator.plusEvaluation(be: Be): Evaluation<Evaluator> =
	dictionary.valueEvaluation(be.syntax).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(bind: Bind): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, bind).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(break_: Break): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, break_).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(check: Check): Evaluation<Evaluator> =
	valueEvaluation(check.is_).bind { isRhsValue ->
		setEvaluation(value.checkValue(isRhsValue.isBoolean))
	}

fun Evaluator.plusEvaluation(@Suppress("UNUSED_PARAMETER") comment: Comment): Evaluation<Evaluator> =
	evaluation

fun Evaluator.plusEvaluation(do_: Do): Evaluation<Evaluator> =
	dictionary.applyEvaluation(do_.block, value).bind { setEvaluation(it) }

fun Evaluator.plusContentOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	value.orNullIf { !isEmpty }?.run {
		rhs.valueOrNull?.structureOrNull?.value?.let { value ->
			setEvaluation(value)
		}
	}	?: evaluation(null)

fun Evaluator.plusEvaluateOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	value.orNullIf { !isEmpty }?.let {
		rhs.valueOrNull?.let { value ->
			dictionary.valueEvaluation(value.script.syntax).bind { evaluated ->
				setEvaluation(evaluated)
			}
		}
	}?:evaluation(null)

fun Evaluator.plusEvaluation(example: Example): Evaluation<Evaluator> =
	dictionary.valueEvaluation(example.syntax).bind { evaluation }

fun Evaluator.plusEvaluation(fail: Fail): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, fail).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(matching: Matching): Evaluation<Evaluator> =
	dictionary.fieldEvaluation(value, matching).bind { plusResolveEvaluation(it) }

fun Evaluator.plusEvaluation(test: Test): Evaluation<Evaluator> =
	dictionary.unitEvaluation(test).map { this }

fun Evaluator.plusEvaluation(get: Get): Evaluation<Evaluator> =
	setEvaluation(value.apply(get))

fun Evaluator.plusEvaluation(give: Give): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, give).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(syntaxField: SyntaxField): Evaluation<Evaluator> =
	if (syntaxField.rhsSyntax.isEmpty)
		setEvaluation(value()).bind {
			it.plusEvaluation(syntaxField.name fieldTo value)
		}
	else dictionary.fieldEvaluation(syntaxField).bind { field ->
		plusEvaluation(field)
	}

fun Evaluator.plusEvaluation(field: Field): Evaluation<Evaluator> =
	plusDynamicOrNullEvaluation(field).or {
		plusResolveEvaluation(field)
	}

fun Evaluator.plusEvaluation(let: Let): Evaluation<Evaluator> =
	dictionary.definitionEvaluation(let).bind {
		set(context.plus(it)).evaluation
	}

fun Evaluator.plusEvaluation(loop: Loop): Evaluation<Evaluator> =
	dictionary.applyEvaluation(loop, value).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(doing: Doing): Evaluation<Evaluator> =
	plusResolveEvaluation(field(dictionary.function(body(doing.block))))

fun Evaluator.plusHashOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	value.orNullIf { !isEmpty }?.let {
		rhs.valueOrNull?.let { value ->
			setEvaluation(value.hashValue)
		}
	}?: evaluation(null)

fun Evaluator.plusEvaluation(quote: Quote): Evaluation<Evaluator> =
	setEvaluation(value.plus(quote.script.value))

fun Evaluator.plusEvaluation(switch: Switch): Evaluation<Evaluator> =
	dictionary.evaluation(value, switch).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(try_: Try): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, try_).bind { setEvaluation(it) }

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

fun Evaluator.plusEvaluation(any: SyntaxAny): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, any).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(as_: As): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, as_).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(is_: Is): Evaluation<Evaluator> =
	valueEvaluation(is_).bind { setEvaluation(it) }

fun Evaluator.valueEvaluation(is_: Is): Evaluation<Value> =
	isValueEvaluation(is_.rhs).map { it.runIf(is_.negated) { isNegate } }

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
	dictionary.valueEvaluation(matching.syntax).bind { matchingValue ->
		value.matches(matchingValue).isValue.evaluation
	}

fun Evaluator.isValueEvaluation(syntax: Syntax): Evaluation<Value> =
	dictionary.valueEvaluation(syntax).bind { rhsValue ->
		dictionary.resolveEvaluation(value.plus(isName fieldTo rhsValue))
	}

fun Evaluator.plusEvaluation(private: Private): Evaluation<Evaluator> =
	context.private.evaluatorEvaluation(private.syntax).map { evaluator ->
		use(evaluator.context.publicDictionary)
	}

fun Evaluator.plusEvaluation(recurse: Recurse): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, recurse).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(repeat: Repeat): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, repeat).bind { setEvaluation(it) }

fun Evaluator.plusValueOrNullEvaluation(rhs: Rhs): Evaluation<Evaluator?> =
	value.orNullIf { !isEmpty }?.let {
		rhs.valueOrNull?.textOrNull?.let { text ->
			value(valueName fieldTo text.scriptOrThrow.value)
		}
	}
		.evaluation
		.nullableBind { setEvaluation(it) }

fun Evaluator.plusEvaluation(with: With): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, with).bind { setEvaluation(it) }

val Evaluator.dictionary
	get() =
		context.privateDictionary

fun Evaluator.plusEvaluation(use: Use): Evaluation<Evaluator> =
	use.dictionaryEvaluation.map { use(it) }

fun Evaluator.use(dictionary: Dictionary): Evaluator =
	set(context.plusPrivate(dictionary))