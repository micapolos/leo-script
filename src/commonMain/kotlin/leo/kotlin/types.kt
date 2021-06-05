package leo.kotlin

import leo.Dict
import leo.TypeField
import leo.array
import leo.base.map
import leo.base.stack
import leo.dict
import leo.pairSeq

data class GeneratedType(
	val name: Name,
	val kotlin: Kotlin)

data class Types(
	val generatedTypes: Dict<TypeField, GeneratedType>,
	val nameCounts: Dict<String, Int>)

fun types() = Types(dict(), dict())

fun Types.plus(typeField: TypeField, generatedType: GeneratedType): Types =
	copy(generatedTypes = generatedTypes.put(typeField to generatedType))

val Types.kotlin: Kotlin get() =
	kotlin(generatedTypes.pairSeq.map { second.kotlin.string }.stack.array.joinToString("\n"))
