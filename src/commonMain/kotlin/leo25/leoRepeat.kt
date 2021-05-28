package leo25

import leo.base.Effect

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

val <V> Leo<V>.repeat: Leo<V>
	get() =
		Leo { environment ->
			throw RepeatException(run(environment))
		}