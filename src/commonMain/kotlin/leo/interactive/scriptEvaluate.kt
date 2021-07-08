package leo.interactive

import leo.Script
import leo.base.effect
import leo.base.notNullOrError
import leo.context
import leo.environment
import leo.evaluator
import leo.prelude.preludeDictionary
import leo.script
import leo.value

val Script.evaluate: Script
  get() =
    environment()
      .effect(preludeDictionary.context.evaluator(value()))
      .tokenizerEvaluation
      .process(this)
      .state
      .notNullOrError("begin/end error")
      .evaluator
      .value
      .script