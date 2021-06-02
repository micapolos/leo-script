package leo

import kotlin.reflect.KClass

fun Value.matches(type: Type): Boolean =
	when (type) {
		AnyType -> true
		EmptyType -> isEmpty
		is LinkType -> linkOrNull?.matches(type.link)?:false
	}

fun Link.matches(typeLink: TypeLink): Boolean =
	field.matches(typeLink.field) && value.matches(typeLink.type)

fun Field.matches(typeField: TypeField) =
	name == typeField.name &&
			rhs.matches(typeField.rhs)

fun Rhs.matches(typeRhs: TypeRhs) =
	typeRhs.isAny || when (typeRhs) {
		is FunctionTypeRhs -> (this is FunctionRhs) && function.matches(typeRhs.function)
		is KClassTypeRhs -> (this is NativeRhs) && native.matches(typeRhs.kClass)
		is NativeTypeRhs -> (this is NativeRhs) && native == typeRhs.native
		is TypeTypeRhs -> (this is ValueRhs) && value.matches(typeRhs.type)
	}

val TypeRhs.isAny get() =
	(this is TypeTypeRhs) && (type is AnyType)

@Suppress("UNUSED_PARAMETER", "unused")
fun Function.matches(typeFunction: TypeFunction) = true

fun Native.matches(kClass: KClass<*>) =
	kClass.isInstance(any)
