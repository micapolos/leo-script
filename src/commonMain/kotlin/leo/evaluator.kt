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

fun Evaluator.updateValue(doing: (Value) -> Value): Evaluator =
	copy(value = doing(value))

fun Evaluator.set(context: Context): Evaluator =
	copy(context = context)

fun Dictionary.dictionaryEvaluation(syntax: Syntax): Evaluation<Dictionary> =
	context.evaluator().plusEvaluation(syntax).map { it.context.publicDictionary }

fun Dictionary.recursiveEvaluation(syntax: Syntax): Evaluation<DictionaryRecursive> =
	dictionaryEvaluation(syntax).map(::recursive)

fun Dictionary.valueEvaluation(value: Value, syntax: Syntax): Evaluation<Value> =
	context.evaluator(value).plusEvaluation(syntax).map { it.value }

fun Dictionary.fieldEvaluation(field: SyntaxField): Evaluation<Field> =
	valueEvaluation(field.rhsSyntax).map { field.name fieldTo it }

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
		is ApplySyntaxLine -> plusEvaluation(line.apply)
		is ApplyingSyntaxLine -> plusEvaluation(line.applying)
		is AsSyntaxLine -> plusEvaluation(line.as_)
		is AtomSyntaxLine -> plusEvaluation(line.atom)
		//is BeSyntaxLine -> plusEvaluation(line.be)
		is BeingSyntaxLine -> plusEvaluation(line.being)
		is EndSyntaxLine -> plusEvaluation(line.end)
		is CheckSyntaxLine -> plusEvaluation(line.check)
		is CombineWithSyntaxLine -> plusEvaluation(line.combineWith)
		is CombiningWithSyntaxLine -> plusEvaluation(line.combiningWith)
		is CommentSyntaxLine -> plusEvaluation(line.comment)
		is DebugSyntaxLine -> plusEvaluation(line.debug)
		is DoSyntaxLine -> plusEvaluation(line.do_)
		is DoingSyntaxLine -> plusEvaluation(line.doing)
//		is ExampleSyntaxLine -> plusEvaluation(line.example)
//		is FailSyntaxLine -> plusEvaluation(line.fail)
		is GetSyntaxLine -> plusEvaluation(line.get)
		//is GiveSyntaxLine -> plusEvaluation(line.give)
		is HelpSyntaxLine -> plusEvaluation(line.help)
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
		//is TakeSyntaxLine -> plusEvaluation(line.take)
		is TestSyntaxLine -> plusEvaluation(line.test)
		is TrySyntaxLine -> plusEvaluation(line.try_)
		is UpdateSyntaxLine -> plusEvaluation(line.update)
		is UseSyntaxLine -> plusEvaluation(line.use)
		//is WithSyntaxLine -> plusEvaluation(line.with)
	}

fun Evaluator.plusEvaluation(atom: SyntaxAtom): Evaluation<Evaluator> =
	plusValueEvaluation(atom).bind { setEvaluation(it) }

fun Evaluator.plusValueEvaluation(atom: SyntaxAtom): Evaluation<Value> =
	when (atom) {
		is FieldSyntaxAtom -> plusValueEvaluation(atom.field)
		is LiteralSyntaxAtom -> plusValueEvaluation(atom.literal)
	}

fun Evaluator.plusDynamicValueOrNullEvaluation(field: Field): Evaluation<Value>? =
	field.rhs.valueOrNull?.let { rhs ->
		when (field.name) {
			beName -> plusBeValueEvaluationOrNull(rhs)
			contentName -> plusContentValueEvaluationOrNull(rhs)
			evaluateName -> plusEvaluateValueEvaluationOrNull(rhs)
			exampleName -> plusExampleValueEvaluationOrNull(rhs)
			failName -> plusFailValueEvaluationOrNull(rhs)
			giveName -> plusGiveValueEvaluationOrNull(rhs)
			hashName -> plusHashValueEvaluationEvaluation(rhs)
			haveName -> plusHaveValueEvaluation(rhs)
			headName -> plusHeadValueEvaluationOrNull(rhs)
			printName -> plusPrintValueEvaluationOrNull(rhs)
			tailName -> plusTailValueEvaluationOrNull(rhs)
			takeName -> plusTakeValueEvaluationOrNull(rhs)
			textName -> plusTextValueEvaluationOrNull(rhs)
			valueName -> plusValueValueEvaluationOrNull(rhs)
			withName -> plusWithValueEvaluation(rhs)
			else -> null
		}
	}

fun Evaluator.plusTakeValueEvaluationOrNull(rhs: Value): Evaluation<Value>? =
	dictionary.takeValueEvaluationOrNull(value, rhs)

fun Evaluator.plusTextValueEvaluationOrNull(rhs: Value): Evaluation<Value>? =
	value.orNullIf { !isEmpty }.let {
		rhs.resolvePrefixOrNull { name, content ->
			when (name) {
				valueName -> value(field(literal(content.string)))
				nameName -> content.fieldOrNull?.name?.let { value(field(literal(it))) }
				else -> null
			}
		}
	}?.evaluation

@Suppress("unused")
fun Evaluator.plusBeValueEvaluationOrNull(beValue: Value): Evaluation<Value>? =
	beValue.evaluation

fun Evaluator.plusHaveValueEvaluation(haveValue: Value): Evaluation<Value> =
	value.have(haveValue).evaluation

fun Evaluator.plusEvaluation(being: Being): Evaluation<Evaluator> =
	dictionary.valueEvaluation(being.syntax).bind {
		plusResolveEvaluation(field(dictionary.function(binder(being(it)))))
	}

fun Evaluator.plusEvaluation(apply: Apply): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, apply).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(applying: Applying): Evaluation<Evaluator> =
	plusResolveEvaluation(field(dictionary.function(binder(applying(body(applying.block))))))

fun Evaluator.plusEvaluation(end_: End): Evaluation<Evaluator> =
	dictionary.valueEvaluation(value, end_).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(check: Check): Evaluation<Evaluator> =
	valueEvaluation(check.is_).bind { isRhsValue ->
		setEvaluation(value.checkValue(isRhsValue.isBoolean))
	}

fun Evaluator.plusEvaluation(combineWith: CombineWith): Evaluation<Evaluator> =
	dictionary.applyEvaluation(value, binder(doing(body(combineWith.block)))).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(combiningWith: CombiningWith): Evaluation<Evaluator> =
	plusResolveEvaluation(field(dictionary.function(binder(combiningWith(body(combiningWith.block))))))

fun Evaluator.plusEvaluation(@Suppress("UNUSED_PARAMETER") comment: Comment): Evaluation<Evaluator> =
	evaluation

fun Evaluator.plusEvaluation(@Suppress("UNUSED_PARAMETER") debug: Debug): Evaluation<Evaluator> =
	throw DebugError(script(debugName lineTo script(scriptLine)))

fun Evaluator.plusEvaluation(do_: Do): Evaluation<Evaluator> =
	dictionary.applyEvaluation(value, binder(doing(body(do_.block)))).bind { setEvaluation(it) }

fun Evaluator.plusContentValueEvaluationOrNull(rhs: Value): Evaluation<Value>? =
	value.orNullIf { !isEmpty }?.run {
		rhs.structureOrNull?.value?.evaluation
	}

fun Evaluator.plusEvaluateValueEvaluationOrNull(rhs: Value): Evaluation<Value>? =
	value.orNullIf { !isEmpty }?.let {
		dictionary.valueEvaluation(rhs.script.syntax)
	}

fun Evaluator.plusExampleValueEvaluationOrNull(exampleValue: Value): Evaluation<Value> =
	value.evaluation

fun Evaluator.plusFailValueEvaluationOrNull(failValue: Value): Evaluation<Value>? =
	value.orNullIf { !isEmpty }?.let { failValue.failEvaluation() }

fun Evaluator.plusEvaluation(matching: Matching): Evaluation<Evaluator> =
	dictionary.fieldEvaluation(matching).bind { plusResolveEvaluation(it) }

fun Evaluator.plusEvaluation(test: Test): Evaluation<Evaluator> =
	dictionary.unitEvaluation(test).map { this }

fun Evaluator.plusEvaluation(get: Get): Evaluation<Evaluator> =
	setEvaluation(value.apply(get))

fun Evaluator.plusGiveValueEvaluationOrNull(rhs: Value): Evaluation<Value>? =
	dictionary.giveValueEvaluationOrNull(value, rhs)

fun Evaluator.plusEvaluation(@Suppress("UNUSED_PARAMETER") help: Help): Evaluation<Evaluator> =
	throw DebugError(value.plus(script(helpScriptLine).value).script)

fun Evaluator.plusValueEvaluation(syntaxField: SyntaxField): Evaluation<Value> =
	if (syntaxField.rhsSyntax.isEmpty)
		setEvaluation(value()).bind {
			it.plusValueEvaluation(syntaxField.name fieldTo value)
		}
	else dictionary.fieldEvaluation(syntaxField).bind {
		plusValueEvaluation(it)
	}

fun Evaluator.plusValueEvaluation(field: Field): Evaluation<Value> =
	null
		?: plusDynamicValueOrNullEvaluation(field)
		?: plusResolveValueEvaluation(field)

fun Evaluator.plusEvaluation(let: Let): Evaluation<Evaluator> =
	dictionary.definitionEvaluation(let).map { plus(it) }

fun Evaluator.plusEvaluation(repeat: Repeat): Evaluation<Evaluator> =
	dictionary.applyEvaluation(repeat, value).bind { setEvaluation(it) }

fun Evaluator.plusEvaluation(doing: Doing): Evaluation<Evaluator> =
	plusResolveEvaluation(field(dictionary.function(binder(doing(body(doing.block))))))

fun Evaluator.plusHashValueEvaluationEvaluation(rhs: Value): Evaluation<Value>? =
	value.orNullIf { !isEmpty }?.let {
		rhs.hashValue.evaluation
	}

fun Evaluator.plusHeadValueEvaluationOrNull(rhs: Value): Evaluation<Value>? =
	value.orNullIf { !isEmpty }?.let {
		rhs.linkOrNull?.field?.let { value(it).evaluation }
	}

fun Evaluator.plusTailValueEvaluationOrNull(rhs: Value): Evaluation<Value>? =
	value.orNullIf { !isEmpty }?.let {
		rhs.linkOrNull?.value?.evaluation
	}

fun Evaluator.plusPrintValueEvaluationOrNull(rhs: Value): Evaluation<Value>? =
	value.orNullIf { !isEmpty }?.let {
		rhs.also { it.script.print }?.evaluation
	}

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

fun Evaluator.plusValueValueEvaluationOrNull(rhs: Value): Evaluation<Value>? =
	value.orNullIf { !isEmpty }?.let {
		rhs.textOrNull?.let { text ->
			value(valueName fieldTo text.scriptOrThrow.value)
		}
	}?.evaluation

fun Evaluator.plusWithValueEvaluation(withValue: Value): Evaluation<Value> =
	value.plus(withValue).evaluation

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

val Evaluator.begin: Evaluator get() = set(value())