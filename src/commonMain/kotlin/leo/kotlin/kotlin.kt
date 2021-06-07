package leo.kotlin

import leo.Type
import leo.TypeLine
import leo.base.runIf
import leo.get

data class Kotlin(val string: String)
fun kotlin(string: String) = Kotlin(string)
val String.kotlin: Kotlin get() = kotlin(this)

val TypeLine.kotlin: Kotlin get() =
	kotlinGeneration.get(types())

val Type.kotlin: Kotlin get() =
	kotlinGeneration.get(types())

operator fun Kotlin.plus(kotlin: Kotlin) = string.plus(kotlin.string).kotlin

val Boolean.kotlin get() = kotlin(if (this) "true" else "false")

val String.nameKotlin get() = runIf(isReserved) { plus("_") }.kotlin  

val String.isReserved: Boolean get() =
	when (this) {
		"as" -> true
		"is" -> true
		"do" -> true
		"true" -> true
		"false" -> true
		"this" -> true
		else -> false
	}