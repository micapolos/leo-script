package leo.kotlin

import leo.Dict
import leo.Text
import leo.TypeField
import leo.array
import leo.base.map
import leo.base.stack
import leo.dict
import leo.pairSeq
import leo.plus
import leo.text

data class GeneratedType(
	val name: Name,
	val kotlin: Kotlin)

data class Types(
	val generatedTypes: Dict<TypeField, GeneratedType>,
	val methodsText: Text,
	val nameCounts: Dict<String, Int>)

fun types() = Types(dict(), text(), dict())

fun Types.plus(typeField: TypeField, generatedType: GeneratedType): Types =
	copy(generatedTypes = generatedTypes.put(typeField to generatedType))

fun Types.plusMethods(text: Text): Types =
	copy(methodsText = methodsText.plus(text))

val Types.kotlin: Kotlin get() =
	kotlin(generatedTypes.pairSeq.map { second.kotlin.string }.stack.array.joinToString("\n"))
