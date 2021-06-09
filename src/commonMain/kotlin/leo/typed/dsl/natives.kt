package leo.typed.dsl

import leo.onlyOrNull
import leo.typed.Field
import leo.typed.Structure
import leo.typed.Value
import leo.typed.fieldOrNull
import leo.typed.structureOrNull

val Value.name: String get() = structureOrNull!!.fieldStack.onlyOrNull!!.name
fun Structure.get(name: String): Field = fieldStack.onlyOrNull!!.value.structureOrNull!!.fieldOrNull(name)!!