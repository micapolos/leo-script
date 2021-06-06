package leo

data class Function(val dictionary: Dictionary, val body: Body)
@kotlin.jvm.JvmInline value class FunctionRecurse(val function: Function)

fun Dictionary.function(body: Body): Function = Function(this, body)
fun recurse(function: Function) = FunctionRecurse(function)

fun Function.applyEvaluation(value: Value): Evaluation<Value> = dictionary.applyEvaluation(body, value)
fun FunctionRecurse.applyEvaluation(value: Value): Evaluation<Value> = function.applyEvaluation(value.structureOrThrow.value)