package leo.indexed.evalutor

typealias Value = Any?

typealias ValueFn = (List<Value>) -> Value

val Value.valueList: List<Value> get() = (this as List<Value>)
val Value.valueString: String get() = (this as String)
val Value.valueDouble: Double get() = (this as Double)
val Value.valueInt: Int get() = (this as Int)
val Value.valueFn: ValueFn get() = (this as ValueFn)

fun value(fn: ValueFn): ValueFn = fn