package leo.term.compiler.runtime

import leo.term.Evaluator
import leo.term.Scope

val thingEvaluator: Evaluator<Thing> get() = Evaluator(Scope<Thing>::value)
