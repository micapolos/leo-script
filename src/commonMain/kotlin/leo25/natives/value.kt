package leo25.natives

import leo25.array
import leo25.Dictionary
import leo25.Number
import leo25.Value
import leo25.base.map
import leo25.base.stack
import leo25.bindingOrNull
import leo25.field
import leo25.fieldOrNull
import leo25.fieldSeq
import leo25.fieldTo
import leo25.getOrNull
import leo25.literal
import leo25.native
import leo25.nativeName
import leo25.nativeOrNull
import leo25.notName
import leo25.notNullOrThrow
import leo25.numberOrThrow
import leo25.plus
import leo25.resolutionOrNull
import leo25.rhs
import leo25.textOrThrow
import leo25.value
import leo25.valueOrNull

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