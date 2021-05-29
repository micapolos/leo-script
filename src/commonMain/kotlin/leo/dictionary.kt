package leo

import leo.base.Seq
import leo.base.fold
import leo.base.ifOrNull
import leo.base.notNullIf
import leo.base.orIfNull
import leo.base.reverse
import leo.base.runIf
import leo.base.seq

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

fun Dictionary.switchLeo(value: Value, script: Script): Leo<Value> =
	switchLeo(value.switchFieldOrThrow, script)

fun Dictionary.switchLeo(field: Field, script: Script): Leo<Value> =
	when (script) {
		is LinkScript -> switchLeo(field, script.link)
		is UnitScript -> value("nonexaustive").throwError()
	}

fun Dictionary.switchLeo(field: Field, scriptLink: ScriptLink): Leo<Value> =
	switchOrNullLeo(field, scriptLink.line).or {
		switchLeo(field, scriptLink.lhs)
	}

fun Dictionary.switchOrNullLeo(field: Field, scriptLine: ScriptLine): Leo<Value?> =
	when (scriptLine) {
		is FieldScriptLine -> switchOrNullLeo(field, scriptLine.field)
		is LiteralScriptLine -> leo(null)
	}

fun Dictionary.switchOrNullLeo(field: Field, scriptField: ScriptField): Leo<Value?> =
	ifOrNull(field.name == scriptField.string) {
		valueLeo(scriptField.rhs).bind { rhsValue ->
			rhsValue.functionOrThrow.applyLeo(value(field))
		}
	} ?: leo(null)

fun Dictionary.applyLeo(body: Body, given: Value): Leo<Value> =
	when (body) {
		is FnBody -> body.fn(set(given)).leo
		is BlockBody -> applyLeo(body.block, given)
	}

fun Dictionary.applyLeo(block: Block, given: Value): Leo<Value> =
	when (block.typeOrNull) {
		BlockType.REPEATEDLY -> applyRepeatingLeo(block.untypedScript, given)
		BlockType.RECURSIVELY -> applyRecursingLeo(block.untypedScript, given)
		null -> applyUntypedLeo(block.untypedScript, given)
	}

fun Dictionary.applyRepeatingLeo(script: Script, given: Value): Leo<Value> =
	given.leo.valueBindRepeating { repeatingGiven ->
		set(repeatingGiven).valueLeo(script)
	}

fun Dictionary.applyRecursingLeo(script: Script, given: Value): Leo<Value> =
	set(given).plusRecurse(script).valueLeo(script)

fun Dictionary.applyUntypedLeo(script: Script, given: Value): Leo<Value> =
	set(given).valueLeo(script)

fun Dictionary.plusRecurse(script: Script): Dictionary =
	plus(
		definition(
			pattern(
				script(
					anyName lineTo script(),
					recurseName lineTo script()
				)
			),
			binding(function(body(BlockType.RECURSIVELY.block(script))))
		)
	)

fun Dictionary.definitionSeqOrNullLeo(scriptField: ScriptField): Leo<Seq<Definition>?> =
	when (scriptField.string) {
		"let" -> letDefinitionOrNull(scriptField.rhs).nullableMap { seq(it) }
		else -> leo(null)
	}

fun Dictionary.letDefinitionOrNull(rhs: Script): Leo<Definition?> =
	null
		?: letDoDefinitionOrNull(rhs)?.leo
		?: letBeDefinitionOrNull(rhs)

fun Dictionary.letDoDefinitionOrNull(rhs: Script): Definition? =
	rhs.matchInfix(doName) { lhs, doRhs ->
		definition(pattern(lhs), binding(function(body(doRhs))))
	}

fun Dictionary.letBeDefinitionOrNull(rhs: Script): Leo<Definition?> =
	rhs.matchInfix(beName) { lhs, beRhs ->
		valueLeo(beRhs).bind { value ->
			definition(pattern(lhs), binding(value)).leo
		}
	} ?: leo(null)

fun Dictionary.updateLeo(value: Value, script: Script): Leo<Value> =
	value.structureOrThrow.let { structure ->
		updateStructureLeo(structure.value, script).map { rhs ->
			value(structure.name fieldTo rhs)
		}
	}

fun Dictionary.updateStructureLeo(value: Value, script: Script): Leo<Value> =
	value.leo.fold(script.lineSeq.reverse) { line ->
		line.fieldOrNull.notNullOrThrow { value(line.field) }.let { fieldOrNull ->
			bind { value ->
				updateStructureLeo(value, fieldOrNull)
			}
		}
	}

fun Dictionary.updateStructureLeo(value: Value, scriptField: ScriptField): Leo<Value> =
	when (value) {
		EmptyValue -> value("no" fieldTo value("field" fieldTo value(scriptField.string))).throwError()
		is LinkValue ->
			updateStructureLeo(value.link, scriptField).map { value(it) }
	}

fun Dictionary.updateStructureLeo(link: Link, scriptField: ScriptField): Leo<Link> =
	if (link.field.name == scriptField.string)
		link.field.rhs.valueOrNull.notNullOrThrow { value(link) }.leo.bind { rhs ->
			context.interpreter(rhs).plusLeo(scriptField.rhs).map { rhsValue ->
				(link.value linkTo (scriptField.string fieldTo rhsValue.value))
			}
		}
	else updateStructureLeo(link.value, scriptField).map { it linkTo link.field }