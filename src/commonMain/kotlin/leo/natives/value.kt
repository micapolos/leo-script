package leo.natives

import leo.Number
import leo.Value
import leo.array
import leo.base.map
import leo.base.stack
import leo.fieldOrNull
import leo.fieldSeq
import leo.fieldTo
import leo.getOrNull
import leo.native
import leo.nativeName
import leo.nativeOrNull
import leo.notName
import leo.notNullOrThrow
import leo.numberOrThrow
import leo.plus
import leo.rhs
import leo.textOrThrow
import leo.value
import leo.valueOrNull

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