package leo25

data class Function(val dictionary: Dictionary, val body: Body)

fun Dictionary.function(body: Body): Function = Function(this, body)

fun Function.applyLeo(value: Value): Leo<Value> = dictionary.applyLeo(body, value)