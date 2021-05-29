package leo.natives

import leo.array
import leo.Dictionary
import leo.Number
import leo.Value
import leo.base.map
import leo.base.stack
import leo.bindingOrNull
import leo.field
import leo.fieldOrNull
import leo.fieldSeq
import leo.fieldTo
import leo.getOrNull
import leo.literal
import leo.native
import leo.nativeName
import leo.nativeOrNull
import leo.notName
import leo.notNullOrThrow
import leo.numberOrThrow
import leo.plus
import leo.resolutionOrNull
import leo.rhs
import leo.textOrThrow
import leo.value
import leo.valueOrNull

fun Dictionary.nativeValue(name: String): Value =
	resolutionOrNull(value(name))!!.bindingOrNull!!.valueOrNull!!

fun Value.nativeValue(name: String): Value =
	getOrNull(name)!!

val Value.nativeText: String get() = textOrThrow
val Value.nativeNumber: Number get() = numberOrThrow
val Value.nativeObject: Any?
	get() = fieldOrNull?.rhs?.nativeOrNull.notNullOrThrow {
		plus(
			notName fieldTo value(
				nativeName
			)
		)
	}.any

val String.nativeValue get() = value(field(literal(this)))
val Number.nativeValue get() = value(field(literal(this)))

val Value.nativeArray: Array<Any?>
	get() =
		fieldOrNull!!.rhs.valueOrNull!!.fieldSeq.map { value(this).nativeArrayElement }.stack.array

val Value.nativeArrayElement: Any?
	get() =
		nativeValue(javaName).nativeObject

val Any?.javaValue: Value
	get() =
		value(javaName fieldTo rhs(native(this)))

val Value.javaObject: Any?
	get() =
		nativeObject