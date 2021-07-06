package leo.term.compiler.native

import leo.term.Evaluator
import leo.term.Scope

val nativeEvaluator: Evaluator<Native> get() = Evaluator(Scope<Native>::value)
