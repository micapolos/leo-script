package leo

import leo.base.effect

fun Leo<Value>.valueBindRepeating(fn: (Value) -> Leo<Value>): Leo<Value> =
	Leo { environment ->
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
