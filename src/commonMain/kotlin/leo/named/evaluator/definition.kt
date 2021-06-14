package leo.named.evaluator

import leo.Type
import leo.base.notNullIf
import leo.named.value.Value
import leo.named.value.ValueLine
import leo.named.value.name
import leo.named.value.value
import leo.type

data class Definition(val type: Type, val value: Value) { override fun toString() = scriptLine.toString() }

fun definition(type: Type, value: Value) =
	Definition(type, value)

fun Definition.valueLineOrNull(type: Type): Value? =
	notNullIf(this.type == type) { value }

val ValueLine.definition: Definition get() =
	definition(name.type, value(this))
