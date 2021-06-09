package leo.typed

import leo.Stack
import leo.base.ifOrNull
import leo.base.notNullIf
import leo.first
import leo.isEmpty
import leo.numberName
import leo.push
import leo.stack
import leo.textName

data class Structure(val fieldStack: Stack<Field>) {
	override fun toString() = script.toString()
}

data class Field(val name: String, val value: Value) {
	override fun toString() = scriptLine.toString()
}

sealed class Value {
	override fun toString() = script.toString()
}

data class NativeValue(val any: Any): Value() {
	override fun toString() = super.toString()
}

data class StructureValue(val structure: Structure): Value() {
	override fun toString() = super.toString()
}

val Stack<Field>.structure get() = Structure(this)
fun Structure.plus(field: Field) = fieldStack.push(field).structure
fun structure(vararg fields: Field) = stack(*fields).structure

infix fun String.fieldTo(value: Value) = Field(this, value)
infix fun String.fieldTo(structure: Structure) = this fieldTo structure.value
val Any.nativeValue: Value get() = NativeValue(this)
val Structure.value: Value get() = StructureValue(this)

val String.value get() = nativeValue
val Double.value get() = nativeValue
val Int.value get() = toDouble().value

val String.textField: Field get() = textName fieldTo value
val Double.numberField: Field get() = numberName fieldTo value
val Int.numberField: Field get() = toDouble().numberField

val textField: Field get() = textName fieldTo String::class.nativeValue
val numberField: Field get() = numberName fieldTo Double::class.nativeValue

val Value.nativeOrNull: Any? get() = (this as? NativeValue)?.any
val Value.structureOrNull: Structure? get() = (this as? StructureValue)?.structure

val Structure.isEmpty: Boolean get() = fieldStack.isEmpty

val Value.isEmpty: Boolean get() =
	when (this) {
		is NativeValue -> false
		is StructureValue -> structure.isEmpty
	}

fun Structure.normalizeOrNull(field: Field): Structure? =
	notNullIf(field.value.isEmpty) {
		structure(field.name fieldTo value)
	}

fun Structure.fieldOrNull(name: String): Field? = fieldStack.first { it.name == name }
fun Field.getOrNull(name: String): Field? = structureOrNull?.fieldOrNull(name)
val Field.structureOrNull: Structure? get() = value.structureOrNull

val Field.doubleOrNull: Double? get() = ifOrNull(name == numberName) { value.nativeOrNull as? Double }
val Field.stringOrNull: String? get() = ifOrNull(name == textName) { value.nativeOrNull as? String }
