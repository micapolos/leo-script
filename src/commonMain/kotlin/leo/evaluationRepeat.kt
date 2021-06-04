package leo

import leo.base.effect
import leo.base.set

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

fun Evaluation<Value>.loop(fn: (Value) -> Evaluation<Breakable<Value>>): Evaluation<Value> =
	Evaluation { environment ->
		run(environment).let { effect ->
			var repeatedEffect = effect
			while (true) {
				val evaluatedIteration = fn(repeatedEffect.value)
				val repeatedEvaluation = evaluatedIteration.run(effect.state)
				repeatedEffect = repeatedEffect.set(repeatedEvaluation.value.value)
				if (repeatedEvaluation.value.shouldBreak) break
			}
			repeatedEffect
		}
	}
