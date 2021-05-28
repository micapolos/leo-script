package leo25.natives

import leo.base.map
import leo.base.stack
import leo13.array
import leo14.Number
import leo14.literal
import leo25.*

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