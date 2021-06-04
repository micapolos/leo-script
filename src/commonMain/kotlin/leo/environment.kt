package leo

import leo.base.Effect
import leo.base.effect

data class Environment(
	val fileLibraryMap: Dict<Use, Dictionary>,
	val trace: Trace)

fun environment(fileLibraryMap: Dict<Use, Dictionary> = dict()) =
	Environment(fileLibraryMap, emptyTrace)

fun Environment.dictionaryEffect(use: Use): Effect<Environment, Dictionary> =
	fileLibraryMap.get(use)
		?.let { this effect it }
		?: use.dictionary.let { dictionary ->
			copy(fileLibraryMap = fileLibraryMap.put(use to dictionary)) effect dictionary
		}
