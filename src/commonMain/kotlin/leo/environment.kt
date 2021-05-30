package leo

import leo.base.Effect
import leo.base.effect
import leo.base.orIfNull

data class Environment(
	val fileLibraryMap: Dict<Use, Dictionary> = dict(),
	val traceOrNull: Trace? = null,
)

fun environment(
	fileLibraryMap: Dict<Use, Dictionary> = dict(),
	traceOrNull: Trace? = null
) =
	Environment(
		fileLibraryMap,
		traceOrNull
	)

fun Environment.libraryEffect(use: Use): Effect<Environment, Dictionary> =
	fileLibraryMap.get(use)
		?.let { this effect it }
		?: use.dictionary.let { dictionary ->
			copy(fileLibraryMap = fileLibraryMap.put(use to dictionary)) effect dictionary
		}

val Value.tracedEvaluation: Evaluation<Unit>
	get() =
		Evaluation { it.copy(traceOrNull = it.traceOrNull?.push(this)) effect Unit }

val traceValueEvaluation: Evaluation<Value>
	get() =
		Evaluation { it effect it.traceOrNull?.value.orIfNull { value(traceName fieldTo value(disabledName)) } }
