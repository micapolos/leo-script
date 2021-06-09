package leo.typed

import leo.Stack
import leo.base.ifOrNull
import leo.isEmpty
import leo.mapOrNull
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

data class Value(val body: Body, val typeValueOrNull: Value?) {
	override fun toString() = script.toString()
}

sealed class Body {
	override fun toString() = script.toString()
}

data class NativeBody(val any: Any): Body() {
	override fun toString() = super.toString()
}

data class StructureBody(val structure: Structure): Body() {
	override fun toString() = super.toString()
}

val Stack<Field>.structure get() = Structure(this)
fun Structure.plus(field: Field) = fieldStack.push(field).structure
fun structure(vararg fields: Field) = stack(*fields).structure
infix fun Body.of(value: Value?) = Value(this, value)

infix fun String.fieldTo(value: Value) = Field(this, value)
infix fun String.fieldTo(structure: Structure) = this fieldTo structure.body.of(structure.typeStructureOrNull?.body?.of(null))
val Any.nativeBody: Body get() = NativeBody(this)
val Structure.body: Body get() = StructureBody(this)
val Any.nativeValue: Value get() = nativeBody of null

val String.field: Field get() = textName fieldTo nativeBody.of(String::class.nativeBody.of(null))
val Double.field: Field get() = numberName fieldTo nativeBody.of(Double::class.nativeBody.of(null))
val Int.field: Field get() = toDouble().field

val Body.nativeOrNull: Any? get() = (this as? NativeBody)?.any
val Body.structureOrNull: Structure? get() = (this as? StructureBody)?.structure

val textField: Field get() = textName fieldTo String::class.nativeBody.of(null)
val numberField: Field get() = numberName fieldTo Double::class.nativeBody.of(null)

val Field.typeFieldOrNull: Field? get() = value.typeValueOrNull?.let { name fieldTo it }
val Structure.isEmpty: Boolean get() = fieldStack.isEmpty
val Structure.typeStructureOrNull: Structure? get() = ifOrNull(!isEmpty) { fieldStack.mapOrNull { typeFieldOrNull }?.structure }