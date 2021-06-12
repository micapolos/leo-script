package leo.named.evaluator

import leo.TypeStructure
import leo.base.notNullIf
import leo.named.value.ValueLine
import leo.named.value.name
import leo.typeStructure

data class Definition<out T>(val typeStructure: TypeStructure, val line: ValueLine<T>)

fun <T> definition(typeStructure: TypeStructure, line: ValueLine<T>) =
	Definition(typeStructure, line)

fun <T> Definition<T>.valueLineOrNull(typeStructure: TypeStructure): ValueLine<T>? =
	notNullIf(this.typeStructure == typeStructure) { line }

val <T> ValueLine<T>.definition: Definition<T> get() =
	definition(name.typeStructure, this)