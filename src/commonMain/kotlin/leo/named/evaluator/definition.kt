package leo.named.evaluator

import leo.TypeStructure
import leo.base.notNullIf
import leo.named.value.Value
import leo.named.value.name
import leo.typeStructure

data class Definition<out T>(val typeStructure: TypeStructure, val value: Value<T>)

fun <T> definition(typeStructure: TypeStructure, value: Value<T>) =
	Definition(typeStructure, value)

fun <T> Definition<T>.valueOrNull(typeStructure: TypeStructure): Value<T>? =
	notNullIf(this.typeStructure == typeStructure) { value }

val <T> Value<T>.definition: Definition<T> get() =
	definition(name.typeStructure, this)