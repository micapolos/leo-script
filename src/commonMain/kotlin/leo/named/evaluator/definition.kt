package leo.named.evaluator

import leo.Type
import leo.base.notNullIf
import leo.named.value.Value
import leo.named.value.ValueLine
import leo.named.value.name
import leo.named.value.value
import leo.type

data class Definition(val type: Type, val value: Value)

fun definition(typeStructure: Type, value: Value) =
	Definition(typeStructure, value)

fun Definition.valueLineOrNull(type: Type): Value? =
	notNullIf(this.type == type) { value }

val ValueLine.definition: Definition get() =
	definition(name.type, value(this))