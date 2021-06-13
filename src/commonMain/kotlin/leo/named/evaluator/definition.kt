package leo.named.evaluator

import leo.Type
import leo.base.notNullIf
import leo.named.value.Value
import leo.named.value.ValueLine
import leo.named.value.name
import leo.named.value.value
import leo.type

data class Definition<out T>(val type: Type, val value: Value<T>)

fun <T> definition(typeStructure: Type, value: Value<T>) =
	Definition(typeStructure, value)

fun <T> Definition<T>.valueLineOrNull(type: Type): Value<T>? =
	notNullIf(this.type == type) { value }

val <T> ValueLine<T>.definition: Definition<T> get() =
	definition(name.type, value(this))