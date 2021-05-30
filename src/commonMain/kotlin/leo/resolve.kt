package leo

import leo.base.fold
import leo.base.orNull
import leo.base.reverse

fun Dictionary.resolveEvaluation(value: Value): Evaluation<Value> =
	value.tracedEvaluation.bind {
		applyOrNullEvaluation(value).or {
			value.resolveEvaluation
		}
	}

fun Dictionary.applyOrNullEvaluation(value: Value): Evaluation<Value?> =
	resolutionOrNull(value)?.bindingOrNull?.applyEvaluation(value) ?: evaluation(null)

fun Dictionary.resolutionOrNull(token: Token): Resolution? =
	tokenToResolutionMap.get(token)

fun Dictionary.resolutionOrNull(value: Value): Resolution? =
	null
		?: concreteResolutionOrNull(value)
		?: resolutionOrNull(token(anyEnd))

fun Dictionary.concreteResolutionOrNull(value: Value): Resolution? =
	when (value) {
		is EmptyValue -> resolutionOrNull(token(emptyEnd))
		is LinkValue -> resolutionOrNull(value.link)
	}

fun Dictionary.resolutionOrNull(link: Link): Resolution? =
	orNull
		?.resolutionOrNull(link.field)
		?.dictionaryOrNull
		?.resolutionOrNull(link.value)

fun Dictionary.resolutionOrNull(field: Field): Resolution? =
	orNull
		?.resolutionOrNull(token(begin(field.name)))
		?.dictionaryOrNull
		?.resolutionOrNull(field.rhs)

fun Dictionary.resolutionOrNull(rhs: Rhs): Resolution? =
	when (rhs) {
		is ValueRhs -> resolutionOrNull(rhs.value)
		is FunctionRhs -> null
		is NativeRhs -> resolutionOrNull(rhs.native)
		is PatternRhs -> null
	} ?: resolutionOrNull(token(anyEnd))

fun Dictionary.resolutionOrNull(native: Native): Resolution? =
	resolutionOrNull(token(native))

val Resolution.dictionaryOrNull get() = (this as? ResolverResolution)?.dictionary
val Resolution.bindingOrNull get() = (this as? BindingResolution)?.binding

fun Dictionary.set(value: Value): Dictionary =
	fold(value.fieldSeq.reverse) { set(it) }

fun Dictionary.set(line: Field): Dictionary =
	plus(
		definition(
			pattern(script(line.name)),
			binding(value(line))
		)
	)