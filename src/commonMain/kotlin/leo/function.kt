package leo

data class Function(val dictionary: Dictionary, val binder: Binder)

sealed class Binder
data class ApplyingBinder(val applying: BodyApplying): Binder()
data class DoingBinder(val doing: BodyDoing): Binder()
data class CombiningBinder(val combining: BodyCombining): Binder()

data class BodyApplying(val body: Body)
data class BodyDoing(val body: Body)
data class BodyCombining(val body: Body)

fun binder(doing: BodyDoing): Binder = DoingBinder(doing)
fun binder(applying: BodyApplying): Binder = ApplyingBinder(applying)
fun binder(combining: BodyCombining): Binder = CombiningBinder(combining)

fun applying(body: Body) = BodyApplying(body)
fun doing(body: Body) = BodyDoing(body)

fun Dictionary.function(binder: Binder): Function = Function(this, binder)

fun Function.giveEvaluation(value: Value): Evaluation<Value> = dictionary.applyEvaluation(value, binder)
fun Function.push(definitionLet: DefinitionLet) = copy(dictionary = dictionary.plus(LetDefinition(definitionLet)))

val Binder.name get() =
	when (this) {
		is ApplyingBinder -> applyingName
		is DoingBinder -> doingName
		is CombiningBinder -> combiningName
	}