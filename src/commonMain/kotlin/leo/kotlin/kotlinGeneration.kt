package leo.kotlin

import leo.ChoiceType
import leo.StructureType
import leo.Type
import leo.TypeLine
import leo.TypeStructure
import leo.bind
import leo.flat
import leo.map

val TypeLine.kotlinGeneration: Generation<Kotlin> get() =
	typeNameGeneration.bind { _ ->
		typesGeneration.map { it.kotlin }
	}

val Type.kotlinGeneration: Generation<Kotlin> get() =
	when (this) {
		is ChoiceType -> TODO()
		is StructureType -> structure.kotlinGeneration
	}

val TypeStructure.kotlinGeneration: Generation<Kotlin> get() =
	lineStack.map { kotlinGeneration }.flat.bind {
		typesGeneration.bind { it.kotlin.generation }
	}