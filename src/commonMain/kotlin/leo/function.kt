package leo

data class ValueDoing(val dictionary: Dictionary, val body: Body)
data class ValueApplying(val dictionary: Dictionary, val body: Body)

fun Dictionary.doing(body: Body): ValueDoing = ValueDoing(this, body)
fun Dictionary.applying(body: Body): ValueApplying = ValueApplying(this, body)

fun ValueDoing.giveEvaluation(value: Value): Evaluation<Value> = dictionary.applyEvaluation(body, value)
fun ValueDoing.push(definitionLet: DefinitionLet) = copy(dictionary = dictionary.plus(LetDefinition(definitionLet)))