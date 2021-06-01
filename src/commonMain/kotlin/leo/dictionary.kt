package leo

import leo.base.fold
import leo.base.ifOrNull
import leo.base.notNullIf
import leo.base.nullOf
import leo.base.orIfNull
import leo.base.runIf
import leo.natives.nativeValue

data class Dictionary(val tokenToResolutionMap: Dict<Token, Resolution>)

sealed class Token
data class BeginToken(val begin: Begin) : Token()
data class EndToken(val end: End) : Token()
data class NativeToken(val native: Native) : Token()

data class Begin(val name: String)

sealed class End
object EmptyEnd : End()
object AnythingEnd : End()

sealed class Resolution
data class ResolverResolution(val dictionary: Dictionary) : Resolution()
data class BindingResolution(val binding: Binding) : Resolution()

fun Dictionary.put(token: Token, resolution: Resolution): Dictionary =
	Dictionary(tokenToResolutionMap.put(token to resolution))

fun dictionary(vararg pairs: Pair<Token, Resolution>): Dictionary =
	Dictionary(dict()).fold(pairs) { put(it.first, it.second) }

fun token(begin: Begin): Token = BeginToken(begin)
fun token(end: End): Token = EndToken(end)
fun token(native: Native): Token = NativeToken(native)

fun begin(name: String) = Begin(name)
val emptyEnd: End = EmptyEnd
val anyEnd: End = AnythingEnd

fun resolution(dictionary: Dictionary): Resolution = ResolverResolution(dictionary)
fun resolution(binding: Binding): Resolution = BindingResolution(binding)

fun Dictionary.update(token: Token, fn: (Resolution?) -> Resolution): Dictionary =
	Dictionary(
		null
			?: tokenToResolutionMap.updateOrNull(token) { fn(it) }
			?: tokenToResolutionMap.put(token to fn(null)))

fun Dictionary.updateContinuation(token: Token, fn: Dictionary.() -> Resolution): Dictionary =
	update(token) { resolutionOrNull ->
		resolutionOrNull?.continuationDictionary.orIfNull { dictionary() }.fn()
	}

val Dictionary.removeForAny: Dictionary
	get() =
		Dictionary(
			tokenToResolutionMap.get(token(anyEnd)).let { resolutionOrNull ->
				if (resolutionOrNull == null) dict()
				else dict<Token, Resolution>().put(token(anyEnd) to resolutionOrNull)
			})

val Resolution.continuationDictionary: Dictionary
	get() =
		when (this) {
			is BindingResolution -> dictionary()
			is ResolverResolution -> dictionary
		}

fun Dictionary.plus(definition: Definition): Dictionary =
	update(definition.pattern.script) {
		resolution(definition.binding)
	}

fun Dictionary.plus(script: Script, body: Body): Dictionary =
	plus(definition(pattern(script), binding(dictionary().function(body))))

fun Dictionary.update(script: Script, fn: Dictionary.() -> Resolution): Dictionary =
	null
		?: updateAnyOrNull(script, fn)
		?: updateExact(script, fn)

fun Dictionary.updateExact(script: Script, fn: Dictionary.() -> Resolution): Dictionary =
	when (script) {
		is UnitScript -> updateContinuation(token(emptyEnd), fn)
		is LinkScript -> update(script.link, fn)
	}

fun Dictionary.update(link: ScriptLink, fn: Dictionary.() -> Resolution): Dictionary =
	update(link.line) {
		resolution(
			update(link.lhs, fn)
		)
	}

fun Dictionary.update(line: ScriptLine, fn: Dictionary.() -> Resolution): Dictionary =
	when (line) {
		is FieldScriptLine -> update(line.field, fn)
		is LiteralScriptLine -> update(line.literal, fn)
	}

fun Dictionary.update(literal: Literal, fn: Dictionary.() -> Resolution): Dictionary =
	updateContinuation(token(begin(literal.selectName))) {
		resolution(updateContinuation(token(literal.native), fn))
	}

fun Dictionary.updateAnyOrNull(script: Script, fn: Dictionary.() -> Resolution): Dictionary? =
	notNullIf(script == script(anyName)) {
		updateAny(fn)
	}

fun Dictionary.update(field: ScriptField, fn: Dictionary.() -> Resolution): Dictionary =
	updateContinuation(token(begin(field.string))) {
		resolution(update(field.rhs, fn))
	}

fun Dictionary.updateAny(fn: Dictionary.() -> Resolution): Dictionary =
	removeForAny.updateContinuation(token(anyEnd), fn)

operator fun Dictionary.plus(dictionary: Dictionary): Dictionary =
	runIf(dictionary.resolutionOrNull(token(anyEnd)) != null) { removeForAny }
		.run {
			dictionary.tokenToResolutionMap.pairSeq.fold(this) { dictionary, (token, resolution) ->
				dictionary.update(token) { resolutionOrNull ->
					resolutionOrNull.orNullMerge(resolution)
				}
			}
		}

fun Resolution.merge(resolution: Resolution): Resolution =
	when (resolution) {
		is BindingResolution -> resolution
		is ResolverResolution ->
			when (this) {
				is BindingResolution -> resolution
				is ResolverResolution -> resolution(dictionary.plus(resolution.dictionary))
			}
	}

fun Resolution?.orNullMerge(resolution: Resolution): Resolution =
	this?.merge(resolution) ?: resolution

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
			else applyEvaluation(case.doing.block, value(field))
		}.map { valueOrNull ->
			valueOrNull.notNullOrThrow { value(switchName) }
		}
	}

fun Dictionary.applyEvaluation(body: Body, given: Value): Evaluation<Value> =
	when (body) {
		is FnBody -> try {
			body.fn(set(given)).evaluation
		} catch (throwable: Throwable) {
			throwable.value.failEvaluation()
		}
		is BlockBody -> applyEvaluation(body.block, given)
	}

fun Dictionary.applyEvaluation(block: Block, given: Value): Evaluation<Value> =
	when (block.typeOrNull) {
		BlockType.REPEATEDLY -> applyRepeatingEvaluation(block.syntax, given)
		BlockType.RECURSIVELY -> applyRecursingEvaluation(block.syntax, given)
		null -> applyUntypedEvaluation(block.syntax, given)
	}

fun Dictionary.applyRepeatingEvaluation(syntax: Syntax, given: Value): Evaluation<Value> =
	given.evaluation.valueBindRepeating { repeatingGiven ->
		set(repeatingGiven).valueEvaluation(syntax)
	}

fun Dictionary.applyRecursingEvaluation(syntax: Syntax, given: Value): Evaluation<Value> =
	set(given).plusRecurse(syntax).valueEvaluation(syntax)

fun Dictionary.applyUntypedEvaluation(syntax: Syntax, given: Value): Evaluation<Value> =
	set(given).valueEvaluation(syntax)

fun Dictionary.plusRecurse(syntax: Syntax): Dictionary =
	plus(
		definition(
			pattern(
				script(
					anyName lineTo script(),
					recurseName lineTo script()
				)
			),
			binding(function(body(BlockType.RECURSIVELY.block(syntax))))
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
	binding(function(body(do_.block))).evaluation

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
	value(matchingName fieldTo matching.pattern.script.value).evaluation
