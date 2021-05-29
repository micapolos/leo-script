package leo25

import leo.base.Effect
import leo.base.effect

data class RepeatException(val effect: Effect<Environment, *>) : RuntimeException()

fun <V> Leo<V>.bindRepeating(fn: (V) -> Leo<V>): Leo<V> =
	Leo { environment ->
		run(environment).let { effect ->
			var repeatedEffect = effect
			while (true) {
				val resultLeo = fn(repeatedEffect.value)
				try {
					repeatedEffect = resultLeo.run(effect.state)
					break
				} catch (repeatException: RepeatException) {
					repeatedEffect = repeatException.effect as Effect<Environment, V>
				}
			}
			repeatedEffect
		}
	}

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
