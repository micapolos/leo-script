package leo.named.evaluator

import leo.Type
import leo.base.notNullIf
import leo.named.value.ValueLine
import leo.named.value.name
import leo.type

data class Definition<out T>(val type: Type, val line: ValueLine<T>)

fun <T> definition(typeStructure: Type, line: ValueLine<T>) =
	Definition(typeStructure, line)

fun <T> Definition<T>.valueLineOrNull(type: Type): ValueLine<T>? =
	notNullIf(this.type == type) { line }

val <T> ValueLine<T>.definition: Definition<T> get() =
	definition(name.type, this)