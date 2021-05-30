package leo

data class Function(val dictionary: Dictionary, val body: Body)

fun Dictionary.function(body: Body): Function = Function(this, body)

fun Function.applyEvaluation(value: Value): Evaluation<Value> = dictionary.applyEvaluation(body, value)