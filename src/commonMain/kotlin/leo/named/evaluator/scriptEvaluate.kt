package leo.named.evaluator

import leo.Script
import leo.named.value.script

val Script.evaluate: Script get() = typedValue.script

val Script.preludeEvaluate: Script get() = preludeTypedValue.script
