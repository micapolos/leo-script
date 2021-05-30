package leo

import leo.base.effect

fun Evaluation<Value>.valueBindRepeating(fn: (Value) -> Evaluation<Value>): Evaluation<Value> =
	Evaluation { environment ->
		run(environment).let { effect ->
			var repeatedEffect = effect
			while (true) {
				val resultLeo = fn(repeatedEffect.value)
				repeatedEffect = resultLeo.run(effect.state)
				repeatedEffect.value.fieldOrNull(repeatName)?.rhs?.valueOrNull?.let { value ->
					repeatedEffect = repeatedEffect.state effect value
					value
				}?:break
			}
			repeatedEffect
		}
	}

val Value.repeat: Value
	get() =
		value(repeatName fieldTo this)
