package leo.kotlin

import leo.Dict
import leo.Stack
import leo.Text
import leo.TypeField
import leo.array
import leo.base.map
import leo.base.stack
import leo.dict
import leo.filter
import leo.map
import leo.pairSeq
import leo.push
import leo.stack
import leo.string

data class GeneratedType(
	val name: Name,
	val kotlin: Kotlin)

data class Types(
	val generatedTypes: Dict<TypeField, GeneratedType>,
	val methodTextStack: Stack<Text>,
	val nameCounts: Dict<String, Int>)

fun types() = Types(dict(), stack(), dict())

fun Types.plus(typeField: TypeField, generatedType: GeneratedType): Types =
	copy(generatedTypes = generatedTypes.put(typeField to generatedType))

fun Types.plusMethods(text: Text): Types =
	copy(methodTextStack = methodTextStack.push(text))

val Types.kotlin: Kotlin get() =
	generatedTypes.pairSeq.map { second.kotlin.string }.stack.array.joinToString("\n").let { types ->
		methodTextStack.map { string }.array.joinToString("\n").let { methods ->
			stack(types, methods).filter { !isEmpty() }.array.joinToString("\n").kotlin
		}
	}
