package leo.kotlin

import leo.Dict
import leo.Type

data class GeneratedType(
	val name: String,
	val code: String)

data class Typegen(
	val dict: Dict<Type, GeneratedType>,
	val nameCounts: Dict<String, Int>)