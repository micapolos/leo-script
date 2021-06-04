package leo

import leo.base.set

fun Evaluation<Value>.repeat(fn: (Value) -> Evaluation<Endable<Value>>): Evaluation<Value> =
	Evaluation { environment ->
		run(environment).let { effect ->
			var repeatedEffect = effect
			while (true) {
				val evaluatedIteration = fn(repeatedEffect.value)
				val repeatedEvaluation = evaluatedIteration.run(effect.state)
				repeatedEffect = repeatedEffect.set(repeatedEvaluation.value.value)
				if (repeatedEvaluation.value.shouldEnd) break
			}
			repeatedEffect
		}
	}
