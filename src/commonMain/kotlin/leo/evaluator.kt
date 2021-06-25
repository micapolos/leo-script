package leo

import leo.base.orNullIf
import leo.base.print
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

fun Evaluator.set(value: Value): Evaluator =
	copy(value = value)

fun Evaluator.set(context: Context): Evaluator =
	copy(context = context)

fun Dictionary.dictionaryEvaluation(syntax: Syntax): Evaluation<Dictionary> =
	context.evaluator().plusEvaluation(syntax).map { it.context.publicDictionary }

fun Dictionary.recursiveEvaluation(syntax: Syntax): Evaluation<DictionaryRecursive> =
	dictionaryEvaluation(syntax).map(::recursive)

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
		dictionaryEvaluation.get

val Syntax.dictionaryEvaluation: Evaluation<Dictionary>
	get() =
		preludeDictionary.dictionaryEvaluation(this)

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
		is BindSyntaxLine -> plusEvaluation(line.bind)
		is EndSyntaxLine -> plusEvaluation(line.end)
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
		is LoadSyntaxLine -> plusEvaluation(line.load)
		is RepeatSyntaxLine -> plusEvaluation(line.repeat)
		is MatchingSyntaxLine -> plusEvaluation(line.matching)
		is PrivateSyntaxLine -> plusEvaluation(line.private)
		is RecurseSyntaxLine -> plusEvaluation(line.recurse)
		is RecursiveSyntaxLine -> plusEvaluation(line.recursive)
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
	value
		.optionBindEvaluation { set(it).plusValueEvaluation(atom) }
		.map { set(it) }

fun Evaluator.plusValueEvaluation(atom: SyntaxAtom): Evaluation<Value> =
	when (atom) {
		is FieldSyntaxAtom -> plusValueEvaluation(atom.field)
		is LiteralSyntaxAtom -> plusValueEvaluation(atom.literal)
	}

fun Evaluator.plusDynamicValueOrNullEvaluation(field: Field): Evaluation<Value?> =
	when (field.name) {
		contentName -> plusContentValueOrNullEvaluation(field.rhs)
		evaluateName -> plusEvaluateValueOrNullEvaluation(field.rhs)
		hashName -> plusHashValueOrNullEvaluation(field.rhs)
		headName -> plusHeadValueOrNullEvaluation(field.rhs)
		printName -> plusPrintValueOrNullEvaluation(field.rhs)
		tailName -> plusTailValueOrNullEvaluation(field.rhs)
		textName -> plusTextValueOrNullEvaluation(field.rhs)
		valueName -> plusValueValueOrNullEvaluation(field.rhs)
		else -> evaluation(null)
	}

fun Evaluator.plusEvaluation(take: Take): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, take).bind { setEvaluation(it) }

fun Evaluator.plusTextValueOrNullEvaluation(rhs: Rhs): Evaluation<Value?> =
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

fun Evaluator.plusEvaluation(be: Be): Evaluation<Evaluator> =
	dictionary.valueEvaluation(be.syntax).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(bind: Bind): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, bind).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(end_: End): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, end_).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(check: Check): Evaluation<Evaluator> =
	valueEvaluation(check.is_).bind { isRhsValue ->
		setEvaluation(value.checkValue(isRhsValue.isBoolean))
	}

fun Evaluator.plusEvaluation(@Suppress("UNUSED_PARAMETER") comment: Comment): Evaluation<Evaluator> =
	evaluation

fun Evaluator.plusEvaluation(do_: Do): Evaluation<Evaluator> =
	dictionary.applyEvaluation(do_.block, value).bind { setEvaluation(it) }

fun Evaluator.plusContentValueOrNullEvaluation(rhs: Rhs): Evaluation<Value?> =
	value.orNullIf { !isEmpty }?.run {
		rhs.valueOrNull?.structureOrNull?.value.evaluation
	}	?: evaluation(null)

fun Evaluator.plusEvaluateValueOrNullEvaluation(rhs: Rhs): Evaluation<Value?> =
	value.orNullIf { !isEmpty }?.let {
		rhs.valueOrNull?.let { value ->
			dictionary.valueEvaluation(value.script.syntax)
		}
	}?:evaluation(null)

fun Evaluator.plusEvaluation(example: Example): Evaluation<Evaluator> =
	dictionary.valueEvaluation(example.syntax).bind { evaluation }

fun Evaluator.plusEvaluation(fail: Fail): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, fail).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(matching: Matching): Evaluation<Evaluator> =
	dictionary.fieldEvaluation(matching).bind { plusResolveEvaluation(it) }

fun Evaluator.plusEvaluation(test: Test): Evaluation<Evaluator> =
	dictionary.unitEvaluation(test).map { this }

fun Evaluator.plusEvaluation(get: Get): Evaluation<Evaluator> =
	setEvaluation(value.apply(get))

fun Evaluator.plusEvaluation(give: Give): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, give).bind { setEvaluation(it) }

fun Evaluator.plusValueEvaluation(syntaxField: SyntaxField): Evaluation<Value> =
	if (syntaxField.rhsSyntax.isEmpty)
		setEvaluation(value()).bind {
			it.plusValueEvaluation(syntaxField.name fieldTo value)
		}
	else dictionary.fieldEvaluation(syntaxField).bind { field ->
		plusValueEvaluation(field)
	}

fun Evaluator.plusValueEvaluation(field: Field): Evaluation<Value> =
	plusDynamicValueOrNullEvaluation(field).or {
		plusResolveValueEvaluation(field)
	}

fun Evaluator.plusEvaluation(let: Let): Evaluation<Evaluator> =
	dictionary.definitionEvaluation(let).map { plus(it) }

fun Evaluator.plusEvaluation(repeat: Repeat): Evaluation<Evaluator> =
	dictionary.applyEvaluation(repeat, value).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(doing: Doing): Evaluation<Evaluator> =
	plusResolveEvaluation(field(dictionary.function(body(doing.block))))

fun Evaluator.plusHashValueOrNullEvaluation(rhs: Rhs): Evaluation<Value?> =
	value.orNullIf { !isEmpty }?.let {
		rhs.valueOrNull?.hashValue.evaluation
	}?: evaluation(null)

fun Evaluator.plusHeadValueOrNullEvaluation(rhs: Rhs): Evaluation<Value?> =
	value.orNullIf { !isEmpty }?.let {
		rhs.valueOrNull?.linkOrNull?.field?.let { value(it).evaluation }
	}?: evaluation(null)

fun Evaluator.plusTailValueOrNullEvaluation(rhs: Rhs): Evaluation<Value?> =
	value.orNullIf { !isEmpty }?.let {
		rhs.valueOrNull?.linkOrNull?.value.evaluation
	}?: evaluation(null)

fun Evaluator.plusPrintValueOrNullEvaluation(rhs: Rhs): Evaluation<Value?> =
	value.orNullIf { !isEmpty }?.let {
		rhs.valueOrNull?.let { it.also { value.script.print }.evaluation }
	}?: evaluation(null)

fun Evaluator.plusEvaluation(quote: Quote): Evaluation<Evaluator> =
	setEvaluation(value.script.value.plus(quote.script.value))

fun Evaluator.plusEvaluation(switch: Switch): Evaluation<Evaluator> =
	dictionary.evaluation(value, switch).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(try_: Try): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, try_).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(update: Update): Evaluation<Evaluator> =
	dictionary.evaluation(value, update).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(set: Set): Evaluation<Evaluator> =
	if (value.isEmpty) plusContextEvaluation(set)
	else plusValueEvaluation(set)

fun Evaluator.plusContextEvaluation(set: Set): Evaluation<Evaluator> =
	context.evaluation
		.foldStateful(set.atomSeq) { atom -> dictionary.fieldEvaluation(atom).map { plus(it) } }
		.map { set(it) }

fun Evaluator.plusValueEvaluation(set: Set): Evaluation<Evaluator> =
	dictionary.evaluation(value, set).bind { setEvaluation(it) }

fun Evaluator.plusValueEvaluation(literal: Literal): Evaluation<Value> =
	plusResolveValueEvaluation(field(literal))

fun Evaluator.plusResolveEvaluation(field: Field): Evaluation<Evaluator> =
	plusResolveValueEvaluation(field).bind { setEvaluation(it) }

fun Evaluator.plusResolveValueEvaluation(field: Field): Evaluation<Value> =
	dictionary.resolveEvaluation(value.plus(field))

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

fun Evaluator.plusEvaluation(recursive: Recursive): Evaluation<Evaluator> =
	dictionary.recursiveEvaluation(recursive.syntax).map { plus(definition(it)) }

fun Evaluator.plusValueValueOrNullEvaluation(rhs: Rhs): Evaluation<Value?> =
	value.orNullIf { !isEmpty }?.let {
		rhs.valueOrNull?.textOrNull?.let { text ->
			value(valueName fieldTo text.scriptOrThrow.value)
		}
	}
		.evaluation

fun Evaluator.plusEvaluation(with: With): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, with).bind { setEvaluation(it) }

val Evaluator.dictionary
	get() =
		context.privateDictionary

fun Evaluator.plusEvaluation(use: Use): Evaluation<Evaluator> =
	use.dictionaryEvaluation.map { use(it) }

fun Evaluator.plusEvaluation(load: Load): Evaluation<Evaluator> =
	Use(load.nameStackLink)
		.stringEvaluation
		.bind { preludeDictionary.valueEvaluation(it.scriptOrThrow.syntax) }
		.bind { setEvaluation(value.plus(it)) }

fun Evaluator.use(dictionary: Dictionary): Evaluator =
	set(context.plusPrivate(dictionary))

fun Evaluator.plus(definition: Definition): Evaluator =
	set(context.plus(definition))