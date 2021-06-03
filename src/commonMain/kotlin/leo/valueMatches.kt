package leo

import kotlin.reflect.KClass

fun Value.matches(type: Type): Boolean =
	when (type) {
		AnyType -> true
		EmptyType -> isEmpty
		is LinkType -> linkOrNull?.matches(type.link)?:false
	}

fun Link.matches(typeLink: TypeLink): Boolean =
	field.matches(typeLink.rhsField) && value.matches(typeLink.lhsType)

fun Field.matches(typeField: TypeField) =
	name == typeField.name &&
			rhs.matches(typeField.rhs)

fun Rhs.matches(typeRhs: TypeRhs) =
	typeRhs.isAny || when (typeRhs) {
		is NativeTypeRhs -> (this is NativeRhs) && native == typeRhs.native
		is TypeTypeRhs -> (this is ValueRhs) && value.matches(typeRhs.type)
	}

val TypeRhs.isAny get() =
	(this is TypeTypeRhs) && (type is AnyType)

@Suppress("UNUSED_PARAMETER", "unused")
fun Function.matches(typeFunction: TypeFunction) = true

fun Native.matches(kClass: KClass<*>) =
	kClass.isInstance(any)

// === matches value ===

fun Value.matches(value: Value): Boolean =
	when (value) {
		AnyValue -> true
		EmptyValue -> isEmpty
		is LinkValue -> linkOrNull?.matches(value.link)?:false
	}

fun Link.matches(link: Link): Boolean =
	field.matches(link.field) && value.matches(link.value)

fun Field.matches(field: Field) =
	name == field.name && rhs.matches(field.rhs)

fun Rhs.matches(rhs: Rhs) =
	rhs.isAny || when (rhs) {
		is FunctionRhs -> (this is FunctionRhs) && this == rhs
		is NativeRhs -> (this is NativeRhs) && this == rhs
		is PatternRhs -> (this is PatternRhs) && this == rhs
		is ValueRhs -> (this is ValueRhs) && value.matches(rhs.value)
	}

val Rhs.isAny get() =
	(this is ValueRhs) && (value is AnyValue)

