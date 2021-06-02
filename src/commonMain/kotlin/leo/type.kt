package leo

import leo.base.fold
import kotlin.reflect.KClass

sealed class Type
object EmptyType: Type()
object AnyType: Type()
data class LinkType(val link: TypeLink): Type()

data class TypeLink(val type: Type, val field: TypeField)
data class TypeField(val name: String, val rhs: TypeRhs)

sealed class TypeRhs
data class TypeTypeRhs(val type: Type): TypeRhs()
data class FunctionTypeRhs(val function: TypeFunction): TypeRhs()
data class NativeTypeRhs(val native: Native): TypeRhs()
data class KClassTypeRhs(val kClass: KClass<*>): TypeRhs()

object TypeFunction

val emptyType: Type = EmptyType
val anyType: Type = AnyType
fun Type.plus(field: TypeField): Type = LinkType(this linkTo field)
infix fun Type.linkTo(field: TypeField) = TypeLink(this, field)
fun type(vararg fields: TypeField) = emptyType.fold(fields) { plus(it) }
fun anyType(vararg fields: TypeField) = anyType.fold(fields) { plus(it) }
infix fun String.fieldTo(rhs: TypeRhs) = TypeField(this, rhs)
infix fun String.fieldTo(type: Type) = this fieldTo rhs(type)

fun rhs(type: Type): TypeRhs = TypeTypeRhs(type)
fun rhs(function: TypeFunction): TypeRhs = FunctionTypeRhs(function)
fun typeRhs(native: Native): TypeRhs = NativeTypeRhs(native)
fun rhs(kClass: KClass<*>): TypeRhs = KClassTypeRhs(kClass)

val textTypeField get() = textName fieldTo rhs(String::class)
val numberTypeField get() = numberName fieldTo rhs(Number::class)
