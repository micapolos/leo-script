package leo

import kotlin.reflect.KClass

data class Type(val fieldStack: Stack<TypeField>)

data class TypeField(val name: String, val rhs: TypeRhs)

sealed class TypeRhs
data class TypeTypeRhs(val type: Type): TypeRhs()
data class ArrowTypeRhs(val arrow: TypeArrow): TypeRhs()
data class KClassTypeRhs(val kClass: KClass<*>): TypeRhs()
data class ValueTypeRhs(val value: Value): TypeRhs()

data class TypeArrow(val lhsType: Type, val rhsType: Type)
