package leo.kotlin

import leo.Type
import leo.TypeLine
import leo.get

val TypeLine.kotlin: Kotlin get() =
	kotlinGeneration.get(types())

val TypeLine.typeName: String get() =
	typeNameGeneration.get(types())

val Type.kotlin: Kotlin get() =
	kotlinGeneration.get(types())