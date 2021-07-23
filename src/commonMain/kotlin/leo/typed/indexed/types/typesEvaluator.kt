package leo.typed.indexed.types

import leo.Types
import leo.typed.indexed.Evaluator

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
val typesEvaluator: Evaluator<Types> get() =
    Evaluator(
    { params -> error("") },
    { scope -> error("") })

