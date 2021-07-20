package leo.typed.indexed.types

import leo.Types
import leo.typed.indexed.Evaluator

val typesEvaluator: Evaluator<Types> get() =
    Evaluator { error("") }

