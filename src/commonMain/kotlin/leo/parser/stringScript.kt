package leo.parser

import leo.Evaluation
import leo.Script
import leo.evaluation
import leo.map

val String.scriptOrNull: Script?
  get() =
    preprocessingScriptParser.parsed(this)

val String.scriptOrThrow: Script
  get() =
    preprocessingScriptParser.parseOrThrow(this)

val String.scriptEvaluation: Evaluation<Script>
  get() =
    evaluation.map {
      scriptOrThrow
    }
