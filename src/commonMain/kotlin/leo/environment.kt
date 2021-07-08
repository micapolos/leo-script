package leo

import leo.base.Effect
import leo.base.effect

data class Environment(
  val fileLibraryMap: Dict<Use, Dictionary>,
  val trace: Trace
)

fun environment(fileLibraryMap: Dict<Use, Dictionary> = dict()) =
  Environment(fileLibraryMap, emptyTrace)

fun Environment.dictionaryEffect(use: Use): Effect<Environment, Dictionary> =
  fileLibraryMap.get(use)
    ?.let { this effect it }
    ?: use.dictionaryEvaluation.run(this).let { effect ->
      effect.state.copy(fileLibraryMap = fileLibraryMap.put(use to effect.value)) effect effect.value
    }
