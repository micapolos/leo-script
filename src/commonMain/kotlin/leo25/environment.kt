package leo25

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

val Value.tracedLeo: Leo<Unit>
	get() =
		Leo { it.copy(traceOrNull = it.traceOrNull?.push(this)) effect Unit }

val traceValueLeo: Leo<Value>
	get() =
		Leo { it effect it.traceOrNull?.value.orIfNull { value(traceName fieldTo value(disabledName)) } }
