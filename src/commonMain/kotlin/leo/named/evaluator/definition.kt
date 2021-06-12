package leo.named.evaluator

import leo.TypeStructure
import leo.base.notNullIf

data class Definition<out T>(val typeStructure: TypeStructure, val value: Value<T>)

fun <T> definition(typeStructure: TypeStructure, value: Value<T>) =
	Definition(typeStructure, value)

fun <T> Definition<T>.valueOrNull(typeStructure: TypeStructure): Value<T>? =
	notNullIf(this.typeStructure == typeStructure) { value }